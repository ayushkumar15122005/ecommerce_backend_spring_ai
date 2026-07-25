INSERT INTO categories (name, description) VALUES
('Electronics', 'Gadgets, devices and accessories'),
('Fashion', 'Clothing, footwear and accessories'),
('Books', 'Fiction, non-fiction and educational books'),
('Home', 'Furniture, decor and kitchen essentials'),
('Grocery', 'Daily essentials and packaged food');

-- Admin user (password: Admin@123 -- replace with a real BCrypt hash before running in a real environment)
INSERT INTO users (full_name, email, password, role) VALUES
('Admin User', 'admin@ecommerce.com', '$2a$10$replace_with_real_bcrypt_hash', 'ADMIN');
