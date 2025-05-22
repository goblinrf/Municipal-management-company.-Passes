import { PageDecorator } from './page-decorator.js';

export class AddressPage extends PageDecorator {
    constructor(listPage) {
        super(listPage);
        document.getElementById("addressForm").addEventListener("submit", this.saveAddress.bind(this));
    }

    async showAll() {
        const res = await fetch("http://localhost:8081/api/addresses", {
            credentials: 'include'
        });

        if (!res.ok) throw res;

        const addresses = await res.json();
        this.listPage.clear();
        addresses.forEach(address => {
            this.listPage.addRow(
           address
            );
        });

        this.attachActions();
    }

    attachActions() {
        document.querySelectorAll("[data-action='edit']").forEach(btn => {
            btn.addEventListener("click", () => this.editAddress(btn.dataset.id));
        });
        document.querySelectorAll("[data-action='delete']").forEach(btn => {
            btn.addEventListener("click", () => this.deleteAddress(btn.dataset.id));
        });
    }

    async saveAddress(event) {
        event.preventDefault();

        const id = document.getElementById("addressId").value;
        const payload = {
            street: document.getElementById("street").value,
            entrance: parseInt(document.getElementById("entrance").value)
        };

        const url = id
            ? `http://localhost:8081/api/addresses/${id}`
            : "http://localhost:8081/api/addresses";

        const method = id ? "PUT" : "POST";

        const res = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            credentials: 'include',
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            bootstrap.Modal.getInstance(document.getElementById("addressModal")).hide();
            await this.showAll();
        } else {
            alert("Ошибка при сохранении");
        }
    }

    async editAddress(id) {
        const res = await fetch(`http://localhost:8081/api/addresses/${id}`, {
            credentials: 'include'
        });

        if (res.ok) {
            const address = await res.json();
            document.getElementById("modalTitle").textContent = "Редактировать адрес";
            document.getElementById("addressId").value = address.id;
            document.getElementById("street").value = address.street;
            document.getElementById("entrance").value = address.entrance;
            new bootstrap.Modal(document.getElementById("addressModal")).show();
        } else {
            alert("Ошибка получения данных адреса");
        }
    }

    async deleteAddress(id) {
        if (!confirm("Удалить адрес?")) return;

        const res = await fetch(`http://localhost:8081/api/addresses/${id}`, {
            method: "DELETE",
            credentials: 'include'
        });

        if (res.ok) {
            await this.showAll();
        } else {
            alert("Ошибка при удалении");
        }
    }
}

// Функция для открытия модального окна создания нового адреса
window.openCreateModal = () => {
    document.getElementById("modalTitle").textContent = "Добавить адрес";
    document.getElementById("addressId").value = "";
    document.getElementById("street").value = "";
    document.getElementById("entrance").value = "";
};
