import { Component, OnInit } from '@angular/core';
import { Product } from '../model/product';
import { ProductCategory } from '../model/product-category';
import { ProductService } from '../service/product.service';

@Component({
  selector: 'app-main-body',
  templateUrl: './main-body.component.html',
  styleUrls: ['./main-body.component.css']
})
export class MainBodyComponent implements OnInit {
  public productsCategory: ProductCategory[] = [];

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    console.log("ngOnInit called");

    // Use the correct method from ProductService to fetch categories
    this.productService.getAllProducts().subscribe(
      (data: ProductCategory[]) => {
        this.productsCategory = data;
        console.log('Product categories loaded:', data);
      },
      (error) => {
        console.error('Error fetching product categories:', error);
      }
    );
  }
}
