package br.com.alura.forum.service.impl;

import br.com.alura.forum.entities.ConcursoEntity;
import br.com.alura.forum.entities.ConcursoFailedEntity;
import br.com.alura.forum.models.ConcursoFeign;
import br.com.alura.forum.repositories.ConcursoFailedRepository;
import br.com.alura.forum.repositories.ConcursoRepository;
import br.com.alura.forum.service.feign.ForumServiceFeign;
import br.com.alura.forum.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DefaultDrawnServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDrawnServiceImpl.class);

    @Autowired
    private ForumServiceFeign forumServiceFeign;

    @Autowired
    private ConcursoRepository concursoRepository;

    @Autowired
    private ConcursoFailedRepository concursoFailedRepository;

    @Autowired
    private TendencyServiceImpl tendencyServiceImpl;

    public List<Integer> updateDataController() {
        Integer lastContestDrawn = callApiForLatestContest();
        ConcursoEntity lastSavedInDatabase = concursoRepository.findTopByOrderByContestNumberDesc();

        Integer iterateTimes = (lastContestDrawn - lastSavedInDatabase.getContestNumber());

        //-> índice dos tokens começa em 1, porque o "0" foi usado para chamar o "último concurso".
        int tokenIndex = 1;
        Integer startedUpdatingContest = lastSavedInDatabase.getContestNumber() + 1;
        while (iterateTimes > 0) {

            tokenIndex = restartTokenIndex(tokenIndex);

            ConcursoFeign contestSavedNow = callApiAndSaveByContestNumber(startedUpdatingContest,
                    Constants.TOKEN_LIST.get(tokenIndex));

            calculateDozensNotDrawnAndSave(contestSavedNow, Constants.ALL_POSSIBLE);

            iterateTimes--;
            tokenIndex++;
            startedUpdatingContest++;
        }
        LOG.info("finished update default data...");
        updateTendencyController();
        LOG.info("tendencies default finished");


        return List.of(lastContestDrawn, lastSavedInDatabase.getContestNumber());
    }

    public Integer callApiForLatestContest() {
        ConcursoFeign lastContestDrawn = forumServiceFeign.getLatestResult(Constants.TOKEN_LIST.get(0));
        LOG.info("concurso mais recente <> " + lastContestDrawn.getData().getDraw_number());
        return lastContestDrawn.getData().getDraw_number();
    }

    private int restartTokenIndex(int tokenIndex) {
        if (tokenIndex == Constants.TOKEN_LIST.size()) {
            tokenIndex = 0;
            tenSecondsDelay();
        }
        return tokenIndex;
    }

    private void calculateDozensNotDrawnAndSave(ConcursoFeign currentContest, List<Integer> constantSequence) {
        List<Integer> failuresForContest = sequenceNotIn(currentContest, constantSequence);
        saveDozensFailed(failuresForContest);
    }

    public List<Integer> sequenceNotIn(Object genericContest, List<Integer> constantSequence) {
        List<Integer> failuresForContest = new ArrayList<>();

        failuresForContest.add(ConcursoFeign.class.cast(genericContest).getData().getDraw_number());
        List<Integer> columnsDrawn = ConcursoFeign.feignToListWithoutContest(ConcursoFeign.class.cast(genericContest));
        failuresForContest.addAll(constantSequence.stream()
                .filter(a -> !columnsDrawn.contains(a))
                .collect(Collectors.toList()));

        return failuresForContest;
    }

    private ConcursoFeign callApiAndSaveByContestNumber(Integer contestNumber, String token) {
        ConcursoFeign apiResponse = null;
        try {
            apiResponse = forumServiceFeign.getSingleResult(contestNumber, token);
            saveDozensDrawn(apiResponse);
        } catch (Exception e) {

        }
        return apiResponse;
    }

    private void tenSecondsDelay() {
        try {
            LOG.info("chamando o serviço após 10 segundos...");
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    @Transactional
    private void saveDozensDrawn(ConcursoFeign drawn) {
        try {
            ConcursoEntity entity = ConcursoFeign.feignToEntity(drawn);
            concursoRepository.save(entity);

            LOG.info("contest saved: " + entity.toString());
        } catch (Exception e) {

        }
    }

    @Transactional
    private void saveDozensFailed(List<Integer> contestAndfailures) {
        try {
            ConcursoFailedEntity entity = ConcursoFailedEntity.listToEntity(contestAndfailures);
            concursoFailedRepository.save(entity);

            LOG.info("failure saved: " + entity.toString());
        } catch (Exception e) {

        }
    }

    public void delete() {
        var lastSavedInDatabase = concursoRepository.findTop5ByOrderByContestNumberDesc();
        lastSavedInDatabase.forEach(l -> concursoRepository.delete(l));

    }

    private void updateTendencyController() {
        try {
            var draws = concursoRepository.findTop200ByOrderByContestNumberDesc();
            var calculatedTendencies = tendencyServiceImpl.fullTendenciesFormatted(draws);
            var preparedData = tendencyServiceImpl.prepareToSave(calculatedTendencies,
                    draws.get(0).getClass());
            tendencyServiceImpl.saveTendency(preparedData);
        } catch (Exception e) {
            LOG.info("draw tendency error " + e.getMessage());
        }
        try {
            var failures = concursoFailedRepository.findTop200ByOrderByContestNumberDesc();
            var calculatedTendencies = tendencyServiceImpl.fullTendenciesFormatted(failures);
            var preparedData = tendencyServiceImpl.prepareToSave(calculatedTendencies,
                    failures.get(0).getClass());
            tendencyServiceImpl.saveTendency(preparedData);
        } catch (Exception e) {
            LOG.info("failure tendency error " + e.getMessage());
        }

    }

}



















