*** Settings ***
Library    Selenium2Library

*** Test Cases ***
Valid Login
    Open Browser To Login Page

*** Keywords ***
Open Browser To Login Page
    Open Browser    ${HOME_PAGE}    ${BROWSER}
    Set Selenium Speed    ${DELAY}
    Maximize Browser Window
    Sleep    ${LOAD_PAGE_TIME}
    Title Should Be    Login Page

*** Variables ***
${DELAY}            0
${HOME_PAGE}        http://localhost/
${BROWSER}          chrome
${LOAD_PAGE_TIME}   5