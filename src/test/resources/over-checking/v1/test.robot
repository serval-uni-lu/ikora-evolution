*** Settings ***
Library    Selenium2Library

*** Test Cases ***
Valid Login
    Open Browser To Login Page

*** Keywords ***
Open Browser To Login Page
    Open Browser    http://localhost/    chrome
    Should Be Equal    ${Delay}    0
    Should Be Equal As Numbers    ${Delay}    0
    Set Selenium Speed    ${DELAY}
    Maximize Browser Window
    Title Should Be    Login Page
    Page Should Not Contain Image    id_image

*** Variables ***
${DELAY}      0
