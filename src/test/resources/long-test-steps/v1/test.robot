*** Settings ***
Library    Selenium2Library

*** Test Cases ***
Valid Login
    User "demo" logs in with password "mode"

*** Keywords ***

User "${username}" logs in with password "${password}"
    Open Browser To Login Page
    Input username    ${username}
    Input password    ${password}
    Submit credentials
    Should be on Home Page

Open Browser To Login Page
    Open Browser    ${HOME_PAGE}    ${BROWSER}
    Set Selenium Speed    ${DELAY}
    Maximize Browser Window
    Title Should Be    Login Page
    Location Should Be    ${LOGIN_PAGE}
    Page Should Not Contain Image    avatar

Input Username
    [Arguments]    ${username}
    Input Text    ${USERNAME_FIELD}    ${username}

Input Password
    [Arguments]    ${password}
    Input Text    ${PASSWORD_FIELD}    ${password}

Submit Credentials
    Select Checkbox    check_agree
    Click Button       ${BUTTON_FIELD}
    Click Button       confirm

Should be on Home Page
    Wait Until Element Is Not Visible    loading    10
    Title Should Be    Login Page
    Location Should Be    ${HOME_PAGE}

*** Variables ***
${USERNAME_FIELD}      username_field
${PASSWORD_FIELD}      password_field
${BUTTON_FIELD}        login_button
${DELAY}        0
${LOGIN_PAGE}    http://localhost/login
${HOME_PAGE}    http://localhost/home
${BROWSER}      chrome