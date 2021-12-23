*** Settings ***
Library    Selenium2Library

*** Test Cases ***
Valid Login
    Input username    user
    Input password    password
    Submit Credentials

*** Keywords ***
Input Username
    [Arguments]    ${username}
    Input Text    username_field    ${username}

Input Password
    [Arguments]    ${password}
    Input Text    password_field    ${password}

Submit Credentials
    Click Button    ${BOTTON_FIELD}
