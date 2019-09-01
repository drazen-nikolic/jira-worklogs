# Simple Tool for Exporting Work Logs from Atlassian JIRA
Exporting Work Logs from Atlassian JIRA can be cumbersome or too expensive. 
On the other side, Atlassian offers a useful JIRA REST API, which we can use to 
get all the data we need. 

This simple Spring Boot application can export work logs
from JIRA issues, for a specific user, in the given period. 

The exported format is CSV. It can be easily opened in text processors or in Excel.

# Configuration
In order to use the tool, we'll need to have a proper JIRA user account
having permissions on the JIRA projects into which the work is logged. 
Additionally, the user should have a proper permissions assigned to read work logs.

Note: Instead of user password, we can use Atlassian _ApiToken_. 
See this [link](https://confluence.atlassian.com/cloud/api-tokens-938839638.html) for the description how to create one.

We need to set parameters in [_application.yml_](src/main/resources/application.yml):
* `baseUrl` - Root URL of the JIRA REST API (v2 supported), e.g. _`https://myjira.atlassian.net/rest/api/2`_
* `username` - JIRA Username (of the user having proper privileges set)
* `apiToken` - API Token for the JIRA user (can also be JIRA user password, but is not recommended).

# Starting the Application
We should first build and then start Spring Boot application. One of the options to do so is:

`mvn spring-boot:run`

# Usage
Let's execute the endpoint for exporting work logs:

`http://localhost:8080/work-logs/csv-export?userDisplayName=Firstname%20Lastname&startDate=2019-07-01&endDate=2019-07-31`

Where:
* `userDisplayName` - name of the JIRA user whose issue work logs are being exported
* `startDate` - Period start date (ISO Data format) in which work happened (inclusive) 
* `endDate` - Period end date (ISO Data format) in which work happened (inclusive) 
* `dayOfWeekRegex` - (optional parameter) additional filter, comma-delimited list of week days to export (e.g. Mon,Tue,Wed)

# Result
As a result, we'll download the CSV file, with next columns:

|Work Date|DOW|Hours|User|Issue Key|Issue Summary|Issue Type|Issue Priority|Work Description|
|---------|----|-----|----|----------|---------|-------------|----------|--------------|----------------|
|2019-07-22|Mon|8.00|John Doe|PROJ-123|Show FAQs on web site|Story|Critical|Implementing requested changes.|
|2019-07-23|Tue|1.00|John Doe|PROJ-456|Code review|Story|Major|Reviewing PRs from team members.|

# License
MIT License

# Credits
Thanks to Marius Storm-Olsen for his code sample at [Atlassian forum](https://answers.atlassian.com/questions/87961/how-to-get-list-of-worklogs-through-jira-rest-api).