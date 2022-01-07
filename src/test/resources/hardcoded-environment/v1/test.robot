*** Settings ***
Library    Selenium2Library

*** Test Cases ***
Valid Login
    Open Browser To Login Page

*** Keywords ***
Open Browser To Login Page
    Open Browser   ${HOME_PAGE}    ${BROWSER}
    Set Selenium Speed    0
    Maximize Browser Window
    Title Should Be    Login Page


*** Variables ***
${HOME_PAGE}    http://localhost/
${BROWSER}      chrome