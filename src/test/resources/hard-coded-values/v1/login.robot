*** Settings ***
Documentation     A test suite with a single Gherkin style test.
...
...               This test is functionally identical to the example in
...               valid_login.robot file.

Library           SeleniumLibrary
Test Teardown     Close Browser

*** Test Cases ***
Valid Login
    User \"demo\" logs in with password \"mode\"

*** Keywords ***

User \"${username}\" logs in with password \"${password}\"
    Input username    ${username}
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
