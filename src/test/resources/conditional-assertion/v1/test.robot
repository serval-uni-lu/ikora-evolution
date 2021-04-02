*** Test Cases ***
Some very interesting test
    Make some clever assertion

*** Keywords ***
Make some clever assertion
    Run Keyword If  ${True}  Should be empty  ${Container}

*** Variables ***
${Container}  Not Empty