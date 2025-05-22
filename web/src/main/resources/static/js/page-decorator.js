export class PageDecorator {
    constructor(listPage) {
        this.listPage = listPage;
    }

    setListPage(listPage) {
        this.listPage = listPage;
    }

    clear() {
        return this.listPage.clear();
    }

    addRow(row) {
        return this.listPage.addRow(row);
    }

    async showAll() {
        return this.listPage.showAll();
    }
}
