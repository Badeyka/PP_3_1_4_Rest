$(async function () {
    await showUser();
    await allUsers();
    await newUser();
});

// панель юзера
async function showUser() {
    fetch("http://localhost:8080/api/user")
        .then(res => res.json())
        .then(data => {

            $('#headerUsername').append(data.email);
            let roles = data.roles.map(role => " " + role.name.substring(5));
            $('#headerRoles').append(roles);

            let user = `$(
            <tr>
                <td>${data.id}</td>
                <td>${data.firstName}</td>
                <td>${data.lastName}</td>
                <td>${data.age}</td>
                <td>${data.email}</td>
                <td>${roles}</td>)`;
            $('#userTable').append(user);
        })
}

// таблица юзеров
const table = $('#usersTable');

async function allUsers() {
    table.empty()
    fetch("http://localhost:8080/api/admin")
        .then(res => res.json())
        .then(data => {
            data.forEach(user => {
                let tableUsers = `$(
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.firstName}</td>
                            <td>${user.lastName}</td>
                            <td>${user.age}</td>
                            <td>${user.email}</td>
                            <td>${user.roles.map(role => " " + role.name.substring(5))}</td>
                            <td>
                                <button type="button" class="btn btn-info" data-toggle="modal" id="buttonEdit"
                                data-action="edit" data-id="${user.id}" data-target="#edit">Edit</button>
                            </td>
                            <td>
                                <button type="button" class="btn btn-danger" data-toggle="modal" id="buttonDelete"
                                data-action="delete" data-id="${user.id}" data-target="#delete">Delete</button>
                            </td>
                        </tr>)`;
                table.append(tableUsers);
            })
        })
}

// New User
async function newUser() {

    const form = document.forms["formNewUser"];

    form.addEventListener('submit', addNewUser)

    function addNewUser(e) {
        e.preventDefault();

        fetch("http://localhost:8080/api/admin/new", {
            method: 'POST', headers: {'Content-Type': 'application/json'},

            body: JSON.stringify({
                firstName: form.firstName.value,
                lastName: form.lastName.value,
                age: form.age.value,
                email: form.email.value,
                password: form.password.value,
                role: form.role.value
            })

        }).then(() => {
            form.reset();
            allUsers();
            $('#nav-users-tab').click();
        })
    }
}

// Edit
$('#edit').on('show.bs.modal', ev => {
    let button = $(ev.relatedTarget);
    let id = button.data('id');
    showEditModal(id);
})

async function showEditModal(id) {

    let user = await getUser(id);
    let form = document.forms["formEdit"];
    form.id.value = user.id;
    form.firstName.value = user.firstName;
    form.lastName.value = user.lastName;
    form.age.value = user.age;
    form.email.value = user.email;
    form.password.value = user.password;
}

$(async function () {
    editUser();
});

function editUser() {
    const editForm = document.forms["formEdit"];
    editForm.addEventListener("submit", ev => {
        ev.preventDefault();

        fetch("http://localhost:8080/api/admin/edit", {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},

            body: JSON.stringify({
                id: editForm.id.value,
                firstName: editForm.firstName.value,
                lastName: editForm.lastName.value,
                age: editForm.age.value,
                email: editForm.email.value,
                password: editForm.password.value,
                role: editForm.role.value
            })
        }).then(() => {
            $('#editFormClose').click();
            allUsers();
        })
    })
}

// Delete
$('#delete').on('show.bs.modal', ev => {
    let button = $(ev.relatedTarget);
    let id = button.data('id');
    showDeleteModal(id);
})

async function showDeleteModal(id) {
    let user = await getUser(id);
    let form = document.forms["formDelete"];
    form.id.value = user.id;
    form.firstName.value = user.firstName;
    form.lastName.value = user.lastName;
    form.age.value = user.age;
    form.email.value = user.email;
    form.role.value = user.role;
}

async function getUser(id) {
    let url = "http://localhost:8080/api/admin/users/" + id;
    let response = await fetch(url);
    return await response.json();
}

$(async function () {
    deleteUser();
});

function deleteUser() {
    const deleteForm = document.forms["formDelete"];
    deleteForm.addEventListener("submit", ev => {
        ev.preventDefault();
        fetch("http://localhost:8080/api/admin/delete/" + deleteForm.id.value, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(() => {
                $('#deleteFormClose').click();
                allUsers();
            })
    })
}

