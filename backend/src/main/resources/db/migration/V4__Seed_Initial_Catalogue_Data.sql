-- V4: Seed Default Categories, Brands, and E-Commerce Products for SK-MarketHub Catalogue

INSERT IGNORE INTO categories (id, parent_id, name, slug, description, is_active) VALUES
(1, NULL, 'Electronics', 'electronics', 'Smartphones, Laptops, Audio, and Gadgets', TRUE),
(2, NULL, 'Fashion', 'fashion', 'Clothing, Footwear, and Accessories', TRUE),
(3, NULL, 'Home & Living', 'home-living', 'Furniture, Decor, and Home Essentials', TRUE),
(4, NULL, 'Appliances', 'appliances', 'Kitchen Appliances, Washing Machines, and Smart TVs', TRUE),
(5, NULL, 'Beauty', 'beauty', 'Skincare, Makeup, and Personal Care', TRUE),
(6, NULL, 'Sports', 'sports', 'Fitness Gear, Sports Equipment, and Activewear', TRUE);

INSERT IGNORE INTO brands (id, name, slug, is_active) VALUES
(1, 'Apple', 'apple', TRUE),
(2, 'Samsung', 'samsung', TRUE),
(3, 'Nike', 'nike', TRUE),
(4, 'Sony', 'sony', TRUE),
(5, 'LG', 'lg', TRUE);

INSERT IGNORE INTO products (id, seller_id, category_id, brand_id, name, slug, sku, description, short_description, base_price, sale_price, tax_rate, status, is_featured) VALUES
(100, NULL, 1, 1, 'iPhone 15 Pro Max (256GB - Natural Titanium)', 'iphone-15-pro-max-256gb', 'SKU-IPHONE15-PRO', 'Apple iPhone 15 Pro Max with A17 Pro Chip, 48MP Camera, Titanium Design, and Super Retina XDR Display.', 'A17 Pro Chip, 48MP Camera, Titanium Body', 159900.00, 139900.00, 18.00, 'ACTIVE', TRUE),
(101, NULL, 1, 2, 'Samsung Galaxy S24 Ultra (12GB RAM, 256GB)', 'samsung-galaxy-s24-ultra', 'SKU-SAMS24-ULTRA', 'Samsung Galaxy S24 Ultra AI Smartphone with Titanium Frame, 200MP Quad Camera, and Built-in S Pen.', '200MP Camera, S Pen, Snapdragon 8 Gen 3', 139999.00, 119999.00, 18.00, 'ACTIVE', TRUE),
(102, NULL, 1, 4, 'Sony WH-1000XM5 Wireless Noise Cancelling Headphones', 'sony-wh1000xm5-headphones', 'SKU-SONY-XM5', 'Industry-leading noise canceling headphones with Auto NC Optimizer, 30-hour battery life, and crystal-clear hands-free calling.', '30-Hr Battery, Auto NC, Touch Controls', 34990.00, 26990.00, 18.00, 'ACTIVE', TRUE),
(103, NULL, 2, 3, 'Nike Air Force 1 07 Sneaker (White/Metallic Gold)', 'nike-air-force-1-07', 'SKU-NIKE-AF1', 'The radiance lives on in the Nike Air Force 1 07, the b-ball icon that puts a fresh spin on classic stitched overlays and crisp leather.', 'Iconic Leather Sneakers, Rubber Sole', 11895.00, 8995.00, 12.00, 'ACTIVE', TRUE),
(104, NULL, 4, 5, 'LG 55 Inch 4K Ultra HD Smart OLED TV', 'lg-55-inch-4k-oled-tv', 'SKU-LG-OLED55', 'LG OLED55 4K Smart TV with α8 AI Processor, Dolby Vision & Atmos, WebOS, and 120Hz Gaming Mode.', '55 Inch OLED 4K, 120Hz, Dolby Atmos', 139990.00, 89990.00, 28.00, 'ACTIVE', TRUE),
(105, NULL, 1, 1, 'MacBook Air M3 Chip (13.6-inch, 16GB RAM, 512GB SSD)', 'macbook-air-m3-16gb', 'SKU-MACBOOK-M3', 'Apple MacBook Air with M3 chip, Liquid Retina display, 18-hour battery life, and silent fanless design.', 'M3 Chip, 16GB RAM, 512GB SSD', 134900.00, 118900.00, 18.00, 'ACTIVE', TRUE),
(106, NULL, 3, NULL, 'Ergonomic Premium High-Back Mesh Office Chair', 'ergonomic-office-chair-mesh', 'SKU-CHAIR-OFFICE', 'Adjustable ergonomic mesh chair with 3D lumbar support, breathable mesh, 135-degree recline, and heavy-duty chrome base.', '3D Lumbar Support, Adjustable Armrests', 18999.00, 9999.00, 18.00, 'ACTIVE', TRUE),
(107, NULL, 4, 2, 'Samsung 324L 3-Star Double Door Refrigerator', 'samsung-324l-double-door-refrigerator', 'SKU-SAMS-REF324', 'Samsung Convertible 5-in-1 Digital Inverter Refrigerator with Twin Cooling Plus and Frost Free technology.', 'Convertible 5-in-1, Digital Inverter', 48990.00, 34990.00, 28.00, 'ACTIVE', TRUE);
