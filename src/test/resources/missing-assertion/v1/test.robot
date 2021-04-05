*** Test Cases ***
Valid Login
    Open Browser To Login Page

Invalid Login
    Open Browser To Login Page
    Title Should Be    Error Page

*** Keywords ***
Open Browser To Login Page
    Open Browser    http://localhost/    chrome\
    Set Selenium Speed    ${DELAY}
    Maximize Browser Window

*** Variables ***
${DELAY}      0