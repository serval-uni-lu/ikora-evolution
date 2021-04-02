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
    Input Text    ${PASSWORD_FIELD}    ${password}

Submit Credentials
    Click Button    ${BOTTON_FIELD}

*** Variables ***
${USERNAME_FIELD}      username_field
${PASSWORD_FIELD}      password_field
${BOTTON_FIELD}        login_button