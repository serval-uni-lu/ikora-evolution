*** Settings ***
Library    Selenium2Library

*** Test Cases ***
Valid Login
    I log in with password "mode"

*** Keywords ***

I log in with password "${password}"
    Input username    demo
    Input password    ${password}
    Submit credentials

Input Username
    [Arguments]    ${username}
    Input Text    ${USERNAME_FIELD}    ${username}

Input Password
    [Arguments]    ${password}
    Input Text    password_field    ${password}

Submit Credentials
    Click Button    ${BOTTON_FIELD}

*** Variables ***
${USERNAME_FIELD}      username_field
${BOTTON_FIELD}        login_button
