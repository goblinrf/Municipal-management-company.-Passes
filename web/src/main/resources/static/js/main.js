import { ListPage } from './list-page.js';
import { AddressPage } from './address-page.js';

document.addEventListener("DOMContentLoaded", async () => {
    const listPage = new ListPage("addressList");
    const addressPage = new AddressPage(listPage);
    await addressPage.showAll();
});
