*** Settings ***
Library    Selenium2Library

*** Test Cases ***
Valid Login
    [Setup]    Open Browser To Login Page
    User "demo" logs in with password "mode"

*** Keywords ***
Open Browser To Login Page
    Open Browser    http://localhost/    chrome
    Maximize Browser Window

User "${username}" logs in with password "${password}"
    Input Text    username_field    ${username}
    Input Text    login_button    ${password}
    Click Button    login_button