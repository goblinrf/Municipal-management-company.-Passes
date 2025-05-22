import { BasePage } from './BasePage.js';

export class ListPage extends BasePage {
    constructor(containerId) {
        super();
        this.container = document.getElementById(containerId);
    }
    clear() {
        this.container.innerHTML = "";
    }
   addRow(address) {
       const row = document.createElement("tr");
       row.innerHTML = `
           <td>${address.id}</td>
           <td>${address.street}</td>
           <td>${address.entrance}</td>
   <td>
            <button class="btn btn-sm btn-outline-primary me-2" data-id="${address.id}" data-action="edit">
                ✏️
            </button>
            <button class="btn btn-sm btn-outline-danger" data-id="${address.id}" data-action="delete">
                🗑
            </button>
        </td>
       `;
       this.container.appendChild(row);
   }
}
