$(document).ready(function() {
    addRegisterSelector();

} );

function addRegisterSelector(){
    $('#selectRegisterType').on('change', function() {
        let type = this.value;
        let username = $('#usernameField');
        let name = $('#nameField');
        let email = $('#emailField');
        let pass = $('#createPassword');
        let confirmPass = $('#confirmPassword');
        let createButton = $('#createAccBtn');
        let eik = $('#eikField');
        let companyName = $('#companyName');
        let companyAddr = $('#companyAddress');

        setEverythingToHidden();

        username.removeAttr('hidden');
        pass.removeAttr('hidden');
        confirmPass.removeAttr('hidden');
        email.removeAttr('hidden');
        createButton.removeAttr('hidden');

        if(type === "Счетоводител"){
            name.removeAttr('hidden');
        }else if(type === "Компания"){
            eik.removeAttr('hidden');
            companyName.removeAttr('hidden');
            companyAddr.removeAttr('hidden');
        }else if(type === "Служител в компания"){
            companyName.removeAttr('hidden');
            name.removeAttr('hidden');
            companyName.removeAttr('hidden');
        }
    });
}
function setEverythingToHidden(){
    $('#usernameField').attr("hidden",true);
    $('#nameField').attr("hidden",true);
    $('#emailField').attr("hidden",true);
    $('#createPassword').attr("hidden",true);
    $('#confirmPassword').attr("hidden",true);
    $('#createAccBtn').attr("hidden",true);
    $('#eikField').attr("hidden",true);
    $('#companyName').attr("hidden",true);
    $('#companyAddress').attr("hidden",true);

}