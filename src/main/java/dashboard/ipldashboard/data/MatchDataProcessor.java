package dashboard.ipldashboard.data;

import java.time.LocalDate;

import org.slf4j.*;
import org.springframework.batch.item.ItemProcessor;

import dashboard.ipldashboard.model.Match;

public class MatchDataProcessor implements ItemProcessor<MatchInput, Match>{

    private static final Logger log = LoggerFactory.getLogger(MatchDataProcessor.class);

    @Override
    public Match process(final MatchInput matchInput) throws Exception {
        
        Match match = new Match();

        match.setId(Long.parseLong(matchInput.getId()));
        match.setCity(matchInput.getCity());
        match.setDate(LocalDate.parse(matchInput.getDate()));
        match.setPlayerOfMatch(matchInput.getPlayer_of_match());
        match.setVenue(matchInput.getVenue());

        String firstTeam, secondTeam;

        if("bat".equalsIgnoreCase(matchInput.getToss_decision())){
            firstTeam = matchInput.getToss_winner();
            secondTeam = matchInput.getToss_winner().equalsIgnoreCase(matchInput.getTeam1())? 
                        matchInput.getTeam2() : matchInput.getTeam1();

        }
        else{
            secondTeam = matchInput.getToss_winner();
            firstTeam = matchInput.getToss_winner().equalsIgnoreCase(matchInput.getTeam2())?
                        matchInput.getTeam1() : matchInput.getTeam2();
        }

        match.setTeam1(firstTeam);
        match.setTeam2(secondTeam);

        match.setTossWinner(matchInput.getToss_winner());
        match.setTossDecision(matchInput.getToss_decision());
        match.setMatchWinner(matchInput.getWinner());
        match.setWinByRuns(matchInput.getWin_by_runs());
        match.setWinByWickets(matchInput.getWin_by_wickets());
        match.setUmpire1(matchInput.getUmpire1());
        match.setUmpire2(matchInput.getUmpire2());
        match.setUmpire3(matchInput.getUmpire3());


        return match;

        
    }
    
}
