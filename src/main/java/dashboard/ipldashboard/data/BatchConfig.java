package dashboard.ipldashboard.data;

import javax.sql.DataSource;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import dashboard.ipldashboard.model.Match;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final String[] FIELD_NAMES = new String[] {
            "id", "season", "city", "date", "team1", "team2", "toss_winner", "toss_decision", "result", "dl_applied",
            "winner", "win_by_runs", "win_by_wickets", "player_of_match", "venue", "umpire1", "umpire2", "umpire3"

    };

    @Autowired
    public JobBuilder jobBuilder;

    @Autowired
    public StepBuilder stepBuilder;

    @Bean
    public FlatFileItemReader<MatchInput> reader() {
        return new FlatFileItemReaderBuilder<MatchInput>()
                .name("MatchInputItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(FIELD_NAMES)
                .targetType(MatchInput.class)
                .build();
    }

    @Bean
    public MatchDataProcessor processor() {
        return new MatchDataProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Match> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Match>()
                .sql("INSERT INTO match (id, city, date, team1, team2, toss_winner, toss_decision, match_winner, win_by_runs, win_by_wickets, player_of_match, venue, umpire1, umpire2, umpire3) VALUES (:id, :city, :date, :team1, :team2, :tossWinner, :tossDecision, :matchWinner, :winByRuns, :winByWickets, :playerOfMatch, :venue, :umpire1, :umpire2, :umpire3)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("importUserJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
            FlatFileItemReader<Person> reader, PersonItemProcessor processor, JdbcBatchItemWriter<Person> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Person, Person>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
