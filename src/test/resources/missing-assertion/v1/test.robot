*** Settings ***
Library    Selenium2Library

*** Test Cases ***
Valid Login
    Open Browser To Login Page

*** Keywords ***
Open Browser To Login Page
    Open Browser    http://localhost/    chrome
    Set Selenium Speed    ${DELAY}
    Maximize Browser Window

*** Variables ***
${DELAY}      0