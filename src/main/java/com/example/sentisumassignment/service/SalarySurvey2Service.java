package com.example.sentisumassignment.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import com.example.sentisumassignment.Exception.SalarySurvey2ServiceException;
import com.example.sentisumassignment.model.SalarySurvey2;
import com.example.sentisumassignment.repository.SalarySurvey2Repository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SalarySurvey2Service {

    @Autowired
    private SalarySurvey2Repository salarySurvey2Repository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public String importData(MultipartFile file) throws SalarySurvey2ServiceException {
        List<Map<String, Integer>> list = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            String[] HEADERS = {
                    "Timestamp",
                    "Employment Type",
                    "Company Name",
                    "Company Size - # Employees",
                    "Primary Location (Country)",
                    "Primary Location (City)",
                    "Industry in Company",
                    "Public or Private Company",
                    "Years Experience in Industry",
                    "Years of Experience in Current Company  ",
                    "Job Title In Company",
                    "Job Ladder",
                    "Job Level",
                    "Required Hours Per Week",
                    "Actual Hours Per Week",
                    "Highest Level of Formal Education Completed",
                    "Total Base Salary in 2018 (in USD)",
                    "Total Bonus in 2018 (cumulative annual value in USD)",
                    "Total Stock Options/Equity in 2018 (cumulative annual value in USD)",
                    "Health Insurance Offered",
                    "Annual Vacation (in Weeks)",
                    "Are you happy at your current position?",
                    "Do you plan to resign in the next 12 months?",
                    "What are your thoughts about the direction of your industry?",
                    "Gender",
                    "Final Question: What are the top skills (you define what that means) that you believe will be necessary for job growth in your industry over the next 10 years?",
                    "Have you ever done a bootcamp? If so was it worth it?"
            };

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(HEADERS)
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(reader);

            for (CSVRecord record : records) {
                DateFormat formatter = new SimpleDateFormat("DD/MM/YYYY hh:mm:ss");
                Date date = formatter.parse(record.get("Timestamp"));

                String totalStocks = record.get("Total Stock Options/Equity in 2018 (cumulative annual value in USD)");
                if (StringUtils.isEmpty(totalStocks)) {
                    totalStocks = "0.0";
                }

                String annualVacation = record.get("Annual Vacation (in Weeks)");

                String totalBase = record.get("Total Base Salary in 2018 (in USD)");
                if (StringUtils.isEmpty(totalBase)) {
                    totalBase = "0.0";
                }

                String totalBonus = record.get("Total Bonus in 2018 (cumulative annual value in USD)");
                if (StringUtils.isEmpty(totalBonus)) totalBonus = "0.0";

                Double totalCompensation = (Double.parseDouble(totalStocks) + Double.parseDouble(totalBase)
                        + Double.parseDouble(totalBonus));

                SalarySurvey2 salarySurvey2 = SalarySurvey2.builder()
                        .timestamp(date)
                        .employmentType(record.get("Employment Type"))
                        .companyName(record.get("Company Name"))
                        .companySize(record.get("Company Size - # Employees"))
                        .country(record.get("Primary Location (Country)"))
                        .city(record.get("Primary Location (City)"))
                        .industry(record.get("Industry in Company"))
                        .sector(record.get("Public or Private Company"))
                        .totalExperience(record.get("Years Experience in Industry"))
                        .currentCompanyExperience(record.get("Years of Experience in Current Company  "))
                        .jobTitle(record.get("Job Title In Company"))
                        .jobLadder(record.get("Job Ladder"))
                        .jobLevel(record.get("Job Level"))
                        .requiredHourPerWeek(record.get("Required Hours Per Week"))
                        .actualHourPerWeek(record.get("Actual Hours Per Week"))
                        .highestLevelOfEducation(record.get("Highest Level of Formal Education Completed"))
                        .totalBaseSalary(Double.parseDouble(totalBase))
                        .totalBonus(Double.parseDouble(totalBonus))
                        .totalStocks(Double.parseDouble(totalStocks))
                        .totalCompensation(totalCompensation)
                        .healthInsurance(record.get("Health Insurance Offered"))
                        .annualVacation(annualVacation)
                        .areYouHappyAtYourCurrentPosition(record.get("Are you happy at your current position?"))
                        .doYouPlanToResignInTheNext12Months(record.get("Do you plan to resign in the next 12 months?"))
                        .whatAreYourThoughtsAboutTheDirectionOfYourIndustry(record.get("What are your thoughts about the direction of your industry?"))
                        .gender(record.get("Gender"))
                        .topSkillsNecessaryForGrowth(record.get("Final Question: What are the top skills (you define what that means) that you believe will be necessary for job growth in your industry over the next 10 years?"))
                        .haveYouEverDoneABootcampIfSoWasItWorthIt(record.get("Have you ever done a bootcamp? If so was it worth it?"))
                        .build();
                SalarySurvey2 save = salarySurvey2Repository.save(salarySurvey2);
                System.out.println("saved salary survey 2 " + save);
            }
            return "success";
        } catch (Exception e) {
            log.error("Error while importing data in survey 2 " + e);
            throw new SalarySurvey2ServiceException(e.getMessage());
        }
    }

    public List<SearchHit<SalarySurvey2>> getCompensationDataByFilter(HashMap<String, String> requestMap) throws Exception {
        try {

            BoolQuery.Builder boolBuilder = QueryBuilders.bool();

            Map<String, String> mappingName = Arrays.stream(SalarySurvey2.class.getDeclaredFields())
                    .collect(Collectors.toMap(Field::getName,
                            field -> field
                                    .getAnnotation(org.springframework.data.elasticsearch.annotations.Field.class)
                                    .name())
                    );

            for (String key : requestMap.keySet()) {
                if (StringUtils.contains(key, '[')) {
                    int index = key.indexOf("[");

                    String keyName = mappingName.get(key.substring(0, index));

                    if (Objects.isNull(keyName)) {
                        throw new Exception("field mot present in index");
                    }
                    String condition = key.substring(index + 1, key.length() - 1);

                    Query query;

                    switch (condition) {
                        case "gte" -> query = RangeQuery.of(q ->
                                q.field(keyName)
                                        .gte(JsonData.of(requestMap.get(key)))
                        )._toQuery();

                        case "lte" -> query = RangeQuery.of(q ->
                                q.field(keyName)
                                        .lte(JsonData.of(requestMap.get(key)))
                        )._toQuery();

                        case "gt" -> query = RangeQuery.of(q ->
                                q.field(keyName)
                                        .gt(JsonData.of(requestMap.get(key)))
                        )._toQuery();

                        case "lt" -> query = RangeQuery.of(q ->
                                q.field(keyName)
                                        .lt(JsonData.of(requestMap.get(key)))
                        )._toQuery();

                        default -> query = MatchQuery.of(q ->
                                q.field(keyName)
                                        .query(requestMap.get(key))
                        )._toQuery();
                    }
                    boolBuilder.must(query);
                } else {
                    if (!Objects.equals(requestMap.get(key), "asc") &&
                            !Objects.equals(requestMap.get(key), "desc")) {
                        System.out.println(key);
                        Query query = MatchQuery.of(q ->
                                q.field(mappingName.get(key))
                                        .query(requestMap.get(key))
                        )._toQuery();
                        boolBuilder.must(query);
                    }
                }
            }

            org.springframework.data.elasticsearch.core.query.Query q;

            List<SortOptions> list = new ArrayList<>();

            boolean isSorting = false;
            if (requestMap.containsValue("asc") || requestMap.containsValue("desc")) {
                isSorting = true;
                for (String key : requestMap.keySet()) {
                    if (Objects.equals(requestMap.get(key), "asc") || Objects.equals(requestMap.get(key), "desc")) {

                        SortOptions sortBuilder = new SortOptions.Builder()
                                .field(f ->
                                        f.field(mappingName.get(key))
                                                .order((Objects.equals(requestMap.get(key), "asc")) ? SortOrder.Asc
                                                        : SortOrder.Desc)
                                )
                                .build();

                        list.add(sortBuilder);
                    }
                }

            }

            if (isSorting) {
                q = NativeQuery.builder()
                        .withQuery(boolBuilder.build()._toQuery())
                        .withSort(list)
                        .build();
            } else {
                q = NativeQuery.builder()
                        .withQuery(boolBuilder.build()._toQuery())
                        .build();
            }

            SearchHits<SalarySurvey2> salarySurvey2 = elasticsearchTemplate.search(q, SalarySurvey2.class,
                    IndexCoordinates.of("salary_survey_2"));
            return salarySurvey2.getSearchHits();
        } catch (Exception e) {
            log.error("Error while getting compensation data survey 2 " + e);
            throw new SalarySurvey2ServiceException(e.getMessage());
        }
    }

    public SalarySurvey2 getSparseFieldSet(String fields) {
        try {
            String[] fieldsArray = fields.split(",");
            org.springframework.data.elasticsearch.core.query.Query q = NativeQuery.builder()
                    .withSourceFilter(new FetchSourceFilter(fieldsArray, null))
                    .build();

            SearchHit<SalarySurvey2> salarySurvey2 = elasticsearchTemplate.searchOne(q, SalarySurvey2.class,
                    IndexCoordinates.of("salary_survey_2"));

            return Objects.requireNonNull(salarySurvey2).getContent();
        } catch (Exception e) {
            log.error("Error while getting getSparseFieldSet survey 2 " + e);
            throw new SalarySurvey2ServiceException(e.getMessage());
        }
    }

}
