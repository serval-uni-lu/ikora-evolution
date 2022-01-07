*** Settings ***
Library    Selenium2Library
Library    OperatingSystem

*** Test Cases ***
Valid Login
    [Setup]    Open Browser To Login Page
    User logs in with password

*** Keywords ***
Open Browser To Login Page
    ${jsonfile}=    Get File     /file/path/data.json
    Set Suite Variable    ${jsonfile.username}
    Set Suite Variable    ${jsonfile.password}
    Open Browser    http://localhost/    chrome
    Maximize Browser Window

User logs in with password
    Input Text    username_field    ${username}
    Input Text    login_button    ${password}
    Click Button    login_button