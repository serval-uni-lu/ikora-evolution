*** Settings ***
Library    Selenium2Library

*** Test Cases ***
Valid Login
    [Setup]    Open Browser To Login Page
    User logs in with password

*** Keywords ***
Open Browser To Login Page
    Open Browser    http://localhost/    chrome
    Maximize Browser Window

User logs in with password
    Input Text    username_field    ${username}
    Input Text    login_button    ${password}
    Click Button    login_button

*** Variables ***
${username}    Victoria
${password}    secrets