-- V6: Seed High Quality HD Product Images for All Products

INSERT IGNORE INTO product_images (id, product_id, image_url, alt_text, display_order, is_primary) VALUES
(1, 100, 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=80', 'iPhone 15 Pro Max Natural Titanium', 1, TRUE),
(2, 101, 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=600&auto=format&fit=crop&q=80', 'Samsung Galaxy S24 Ultra Titanium', 1, TRUE),
(3, 102, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80', 'Sony WH-1000XM5 Headphones', 1, TRUE),
(4, 103, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=600&auto=format&fit=crop&q=80', 'Nike Air Force 1 Sneakers White Gold', 1, TRUE),
(5, 104, 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=600&auto=format&fit=crop&q=80', 'LG 55 Inch OLED 4K Smart TV', 1, TRUE),
(6, 105, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&auto=format&fit=crop&q=80', 'Apple MacBook Air M3 Chip', 1, TRUE),
(7, 106, 'https://images.unsplash.com/photo-1580481072645-022f9a6d1270?w=600&auto=format&fit=crop&q=80', 'Ergonomic Mesh High-Back Office Chair', 1, TRUE),
(8, 107, 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=600&auto=format&fit=crop&q=80', 'Samsung Double Door Refrigerator 324L', 1, TRUE);
