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
    Input Text    username_field    ${username}

Input Password
    [Arguments]    ${password}
    Input Text    ${PASSWORD_FIELD}    ${password}

Submit Credentials
    Click Button    login_button

*** Variables ***
${PASSWORD_FIELD}      password_field