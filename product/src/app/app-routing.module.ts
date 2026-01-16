import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainBodyComponent } from './main-body/main-body.component';
import { XboxComponent } from './xbox/xbox.component';
import { ProductCategoryComponent } from './product-category/product-category.component';
import { ShoppingCartComponent } from './shopping-cart/shopping-cart.component';

import { ProductOrderComponent } from './product-order/product-order.component';
import { AccountComponent } from './account/account.component';
import { ContactUsComponent } from './contact-us/contact-us.component';
import { ProductPageComponent } from './product-page/product-page.component';

const routes: Routes = [
  {path:'',component:MainBodyComponent}, 
  {path:'cart',component:ShoppingCartComponent}, 
  {path:'product',component:ProductCategoryComponent}, 
  {path:'order',component:ProductOrderComponent}, 
  {path:'account',component:AccountComponent}, 
  {path:'contact',component:ContactUsComponent},
  {path:'page',component:ProductPageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
