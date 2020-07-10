*** Test Cases ***
Some very interesting test
    Make some clever assertion

*** Keywords ***
Make some cleaver assertion
    Run Keyword If  ${True}  Make the assertion

Make the assertion
    Should be empty  ${Container}

*** Variables ***
${Container}  Not Empty