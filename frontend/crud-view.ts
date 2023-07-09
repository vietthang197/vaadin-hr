import { LitElement, html, css, customElement } from 'lit-element';
import '@vaadin/form-layout/src/vaadin-form-layout.js';
import '@vaadin/text-field/src/vaadin-text-field.js';
import '@vaadin/split-layout/src/vaadin-split-layout.js';
import '@vaadin/vertical-layout/src/vaadin-vertical-layout.js';
import '@vaadin/button/src/vaadin-button.js';

@customElement('crud-view')
export class CrudView extends LitElement {
  static get styles() {
    return css`
      :host {
          display: block;
          height: 100%;
      }
      `;
  }

  render() {
    return html`
<vaadin-vertical-layout theme="spacing" style="width: 100%; height: 100%;">
 <vaadin-form-layout style="padding: var(--lumo-space-m);">
  <vaadin-text-field label="Họ và tên" placeholder="Nhập họ và tên" id="fullName" type="text">
   Nhã
  </vaadin-text-field>
  <vaadin-text-field label="Số diện thoại" placeholder="Nhập số điện thoại" id="phone" type="text"></vaadin-text-field>
  <vaadin-text-field label="Địa chỉ" placeholder="Nhập địa chỉ" id="address" type="text"></vaadin-text-field>
  <vaadin-split-layout theme="minimal"></vaadin-split-layout>
 </vaadin-form-layout>
 <vaadin-vertical-layout theme="spacing" style="width: 100%; margin: var(--lumo-space-m);">
  <vaadin-button theme="primary" id="btnSubmit" style="align-self: center;" tabindex="0">
   Xác nhận
  </vaadin-button>
 </vaadin-vertical-layout>
</vaadin-vertical-layout>
`;
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
