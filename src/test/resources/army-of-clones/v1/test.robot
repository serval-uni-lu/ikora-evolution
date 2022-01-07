*** Settings ***
Library    Selenium2Library

*** Test Cases ***
Valid Login
    User "demo" logs in with password "mode"
    User "Bob" logs in with password "password"

*** Keywords ***

User "demo" logs in with password "mode"
    Input username    demo
    Input password    mode
    Submit credentials

User "Bob" logs in with password "password"
    Input username    Bob
    Input password    password
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
