-- V5: Expand Full E-Commerce Structure (Categories, Subcategories, Coupons, and Delivery Partner Role)

-- 1. Insert Role for Delivery Partner
INSERT IGNORE INTO roles (name, description) VALUES
('DELIVERY_PARTNER', 'Delivery Partner fleet operator');

-- 2. Insert Main Categories
INSERT IGNORE INTO categories (id, parent_id, name, slug, description, is_active) VALUES
(10, NULL, 'Electronics', 'electronics-main', 'Mobiles, Laptops, Audio, and Smart Devices', TRUE),
(20, NULL, 'Fashion', 'fashion-main', 'Men, Women, and Kids Fashion Wear', TRUE),
(30, NULL, 'Home & Kitchen', 'home-kitchen-main', 'Kitchen, Furniture, Decor, and Home Care', TRUE),
(40, NULL, 'Beauty & Personal Care', 'beauty-care-main', 'Makeup, Skincare, Grooming, and Fragrances', TRUE),
(50, NULL, 'Grocery', 'grocery-main', 'Staples, Snacks, Beverages, and Packaged Food', TRUE),
(60, NULL, 'Sports & Fitness', 'sports-fitness-main', 'Gym Equipment, Activewear, and Outdoor Gear', TRUE),
(70, NULL, 'Automotive', 'automotive-main', 'Car & Bike Accessories, Helmets, and Care', TRUE),
(80, NULL, 'Pet Supplies', 'pet-supplies-main', 'Food, Grooming, Toys, and Accessories for Pets', TRUE),
(90, NULL, 'Tools & Hardware', 'tools-hardware-main', 'Hand & Power Tools, Electrical, and Plumbing', TRUE),
(100, NULL, 'Books & Stationery', 'books-stationery-main', 'Books, Office Supplies, and School Products', TRUE),
(110, NULL, 'Toys & Baby', 'toys-baby-main', 'Toys, Diapers, Baby Care, and Furniture', TRUE);

-- 3. Insert Subcategories for Electronics
INSERT IGNORE INTO categories (parent_id, name, slug, description, is_active) VALUES
(10, 'Mobiles', 'electronics-mobiles', 'Smartphones and Mobile Phones', TRUE),
(10, 'Tablets', 'electronics-tablets', 'Android & iOS Tablets', TRUE),
(10, 'Laptops', 'electronics-laptops', 'Gaming and Office Laptops', TRUE),
(10, 'Computers', 'electronics-computers', 'Desktop PCs & Monitors', TRUE),
(10, 'Headphones', 'electronics-headphones', 'Over-ear and On-ear Headphones', TRUE),
(10, 'Earbuds', 'electronics-earbuds', 'True Wireless Stereo Earbuds', TRUE),
(10, 'Smart Watches', 'electronics-smart-watches', 'Fitness Bands and Smart Watches', TRUE),
(10, 'Speakers', 'electronics-speakers', 'Bluetooth & Home Theatre Speakers', TRUE),
(10, 'Cameras', 'electronics-cameras', 'DSLR & Mirrorless Cameras', TRUE),
(10, 'Printers', 'electronics-printers', 'Inkjet and Laser Printers', TRUE),
(10, 'Computer Accessories', 'electronics-computer-accessories', 'Keyboards, Mice, and Cables', TRUE),
(10, 'Mobile Accessories', 'electronics-mobile-accessories', 'Cases, Covers, and Screen Protectors', TRUE),
(10, 'Chargers & Cables', 'electronics-chargers-cables', 'Fast Chargers and USB Type-C Cables', TRUE),
(10, 'Power Banks', 'electronics-power-banks', 'High Capacity Power Banks', TRUE);

-- 4. Insert Subcategories for Fashion
INSERT IGNORE INTO categories (parent_id, name, slug, description, is_active) VALUES
(20, 'Men - T-Shirts', 'fashion-men-tshirts', 'Casual & Printed T-Shirts for Men', TRUE),
(20, 'Men - Shirts', 'fashion-men-shirts', 'Formal & Casual Shirts for Men', TRUE),
(20, 'Men - Jeans', 'fashion-men-jeans', 'Slim Fit & Regular Jeans for Men', TRUE),
(20, 'Men - Shoes', 'fashion-men-shoes', 'Sports & Formal Shoes for Men', TRUE),
(20, 'Women - Sarees', 'fashion-women-sarees', 'Silk, Cotton, and Designer Sarees', TRUE),
(20, 'Women - Kurtis', 'fashion-women-kurtis', 'Anarkali and Straight Kurtis', TRUE),
(20, 'Women - Dresses', 'fashion-women-dresses', 'Western & Party Wear Dresses', TRUE),
(20, 'Women - Handbags', 'fashion-women-handbags', 'Leather Handbags & Totes', TRUE),
(20, 'Kids - Boys Clothing', 'fashion-kids-boys', 'T-Shirts & Shorts for Boys', TRUE),
(20, 'Kids - Girls Clothing', 'fashion-kids-girls', 'Frocks & Tops for Girls', TRUE),
(20, 'Kids - Baby Clothing', 'fashion-kids-baby', 'Rompers & Onesies for Babies', TRUE);

-- 5. Insert Subcategories for Home & Kitchen
INSERT IGNORE INTO categories (parent_id, name, slug, description, is_active) VALUES
(30, 'Kitchen Appliances', 'home-kitchen-appliances', 'Mixer Grinders, Juicers, and Air Fryers', TRUE),
(30, 'Cookware', 'home-cookware', 'Non-stick Pans & Pressure Cookers', TRUE),
(30, 'Furniture', 'home-furniture', 'Sofas, Beds, and Dining Tables', TRUE),
(30, 'Bedsheets', 'home-bedsheets', 'Cotton Double & Queen Bedsheets', TRUE),
(30, 'Home Decor', 'home-decor', 'Wall Art, Vases, and Clocks', TRUE),
(30, 'Lighting', 'home-lighting', 'LED Bulbs & Decorative Lamps', TRUE);

-- 6. Insert Subcategories for Beauty, Grocery, Sports, Automotive, Pet, Tools, Books, Toys
INSERT IGNORE INTO categories (parent_id, name, slug, description, is_active) VALUES
(40, 'Makeup', 'beauty-makeup', 'Lipsticks, Foundations, and Mascara', TRUE),
(40, 'Skincare', 'beauty-skincare', 'Face Washes, Serums, and Moisturizers', TRUE),
(40, 'Men Grooming', 'beauty-mens-grooming', 'Beard Trimmers and Aftershave', TRUE),
(50, 'Atta & Flour', 'grocery-atta-flour', 'Wheat Atta & Specialty Flours', TRUE),
(50, 'Rice & Dal', 'grocery-rice-dal', 'Basmati Rice & Pulses', TRUE),
(50, 'Snacks & Beverages', 'grocery-snacks-beverages', 'Chips, Biscuits, Tea, and Coffee', TRUE),
(60, 'Gym Equipment', 'sports-gym-equipment', 'Dumbbells, Treadmills, and Bands', TRUE),
(60, 'Sports Shoes', 'sports-shoes', 'Running & Football Shoes', TRUE),
(70, 'Helmets', 'automotive-helmets', 'Full Face Rider Helmets', TRUE),
(70, 'Car Accessories', 'automotive-car-accessories', 'Seat Covers & Car Mats', TRUE),
(80, 'Dog Food', 'pet-dog-food', 'Dry & Wet Dog Food', TRUE),
(80, 'Cat Food', 'pet-cat-food', 'Cat Treats & Kibble', TRUE),
(90, 'Hand & Power Tools', 'tools-hand-power-tools', 'Drill Machines, Screwdrivers, & Hardware', TRUE),
(100, 'Books', 'books-general', 'Fiction, Non-fiction, & Exam Prep Books', TRUE),
(100, 'Stationery', 'stationery-office', 'Pens, Notebooks, & Office Supplies', TRUE),
(110, 'Toys & Games', 'toys-games', 'Action Figures, Board Games, & Puzzles', TRUE),
(110, 'Diapers & Baby Care', 'baby-diapers-care', 'Baby Wipes & Soft Diapers', TRUE);

-- 7. Insert Default Coupons as specified in screenshot
INSERT IGNORE INTO coupons (code, discount_type, discount_value, minimum_order, maximum_discount, is_active) VALUES
('SKWELCOME', 'FLAT', 100.00, 499.00, 100.00, TRUE),
('SK10', 'PERCENTAGE', 10.00, 299.00, 500.00, TRUE),
('FIRSTBUY', 'FLAT', 200.00, 999.00, 200.00, TRUE),
('FASHION20', 'PERCENTAGE', 20.00, 799.00, 1000.00, TRUE);
