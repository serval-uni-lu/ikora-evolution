*** Test Cases ***
Valid Login
    Open Browser To Login Page

*** Keywords ***
Open Browser To Login Page
    Open Browser    http://localhost/    chrome
    Set Selenium Speed    ${DELAY}
    Maximize Browser Window
    ${LOGIN PAGE}=    Get Element Attribute    title_page
    Title Should Be    ${LOGIN PAGE}

*** Variables ***
${DELAY}      0