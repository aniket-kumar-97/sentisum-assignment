package com.example.sentisumassignment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "salary_survey_2")
public class SalarySurvey2 {

    @Id
    @Field(type = FieldType.Text, name = "id")
    private String id;
    @Field(type = FieldType.Date, name = "timestamp", pattern = "DD/MM/YYYY hh:mm:ss")
    private Date timestamp;
    @Field(type = FieldType.Text, name = "employment_type")
    private String employmentType;
    @Field(type = FieldType.Text, name = "company_name")
    private String companyName;
    @Field(type = FieldType.Text, name = "company_size")
    private String companySize;
    @Field(type = FieldType.Text, name = "country")
    private String country;
    @Field(type = FieldType.Keyword, name = "city")
    private String city;
    @Field(type = FieldType.Text, name = "industry")
    private String industry;
    @Field(type = FieldType.Text, name = "sector")
    private String sector;
    @Field(type = FieldType.Text, name = "total_experience")
    private String totalExperience;
    @Field(type = FieldType.Text, name = "current_company_experience")
    private String currentCompanyExperience;
    @Field(type = FieldType.Text, name = "job_title")
    private String jobTitle;
    @Field(type = FieldType.Text, name = "job_ladder")
    private String jobLadder;
    @Field(type = FieldType.Text, name = "job_level")
    private String jobLevel;
    @Field(type = FieldType.Text, name = "required_hour_per_week")
    private String requiredHourPerWeek;
    @Field(type = FieldType.Text, name = "actual_hour_per_week")
    private String actualHourPerWeek;
    @Field(type = FieldType.Text, name = "highest_level_of_education")
    private String highestLevelOfEducation;
    @Field(type = FieldType.Double, name = "have_you_ever_done_a_bootcamp_if_so_was_it_worth_ittotal_base_salary")
    private Double totalBaseSalary;
    @Field(type = FieldType.Double, name = "total_bonus")
    private Double totalBonus;
    @Field(type = FieldType.Double, name = "total_stocks")
    private Double totalStocks;
    @Field(type = FieldType.Double, name = "total_compensation")
    private Double totalCompensation;
    @Field(type = FieldType.Text, name = "health_insurance")
    private String healthInsurance;
    @Field(type = FieldType.Text, name = "annual_vacation")
    private String annualVacation;
    @Field(type = FieldType.Text, name = "are_you_happy_at_your_current_position")
    private String areYouHappyAtYourCurrentPosition;
    @Field(type = FieldType.Text, name = "do_you_plan_to_resign_in_the_next_12_months")
    private String doYouPlanToResignInTheNext12Months;
    @Field(type = FieldType.Text, name = "what_are_your_thoughts_about_the_direction_of_your_industry")
    private String whatAreYourThoughtsAboutTheDirectionOfYourIndustry;
    @Field(type = FieldType.Text, name = "gender")
    private String gender;
    @Field(type = FieldType.Text, name = "top_skills_necessary_for_growth")
    private String topSkillsNecessaryForGrowth;
    @Field(type = FieldType.Text, name = "have_you_ever_done_a_bootcamp_if_so_was_it_worth_it")
    private String haveYouEverDoneABootcampIfSoWasItWorthIt;
}
