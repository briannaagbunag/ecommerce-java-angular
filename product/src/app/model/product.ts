export class Product {
    id: number =0;
    name: string = ''
    description: string = ''
    categoryName: string = ''        
    imageFile: string = ''
    price: string = "0.0"
    unitOfMeasure: string = ""
    quantityStock: number = 0; // Added quantityStock to track available stock
products: any;
}
