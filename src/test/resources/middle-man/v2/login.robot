*** Test Cases ***
Valid Login
    User "demo" logs in with password "mode"

*** Keywords ***
User "${username}" logs in with password "${password}"
    [Arguments]    ${username}
    Input Text    username_field    ${username}
