*** Settings ***
Library    Selenium2Library

*** Test Cases ***
Valid Login
    User "demo" logs in with password "mode"

*** Keywords ***
User "${username}" logs in with password "${password}"
    Input username    ${username}
    Input password    ${password}
    Submit credentials

Input Username
    [Arguments]    ${username}
    Input Text    username_field    ${username}

Input Password
    [Arguments]    ${password}
    Input Text    ${PASSWORD_FIELD}    ${password}

Submit Credentials
    Click Button    login_button

*** Variables ***
${PASSWORD_FIELD}      css:.covid-form > div > div.react-grid-Container > div > div > div.react-grid-Header > div > div > div:nth-child(3) > div