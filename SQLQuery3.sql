-- MiniShop: SQL Server database for a small retail store.
-- Run in SQL Server Management Studio (SSMS) or Azure Data Studio.
-- This script creates a NEW database; it does not delete existing data.

IF DB_ID(N'MiniShop') IS NULL
    EXEC(N'CREATE DATABASE MiniShop');
GO
USE MiniShop;
GO

IF OBJECT_ID(N'dbo.Categories', N'U') IS NULL
CREATE TABLE dbo.Categories (
    CategoryId INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL UNIQUE
);
GO

IF OBJECT_ID(N'dbo.Products', N'U') IS NULL
CREATE TABLE dbo.Products (
    ProductId INT IDENTITY(1,1) PRIMARY KEY,
    CategoryId INT NOT NULL,
    Name NVARCHAR(150) NOT NULL,
    Price DECIMAL(12,2) NOT NULL CHECK (Price >= 0),
    Stock INT NOT NULL DEFAULT 0 CHECK (Stock >= 0),
    ImageUrl NVARCHAR(500) NULL,
    IsActive BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_Products_Categories FOREIGN KEY (CategoryId)
        REFERENCES dbo.Categories(CategoryId)
);
GO

IF OBJECT_ID(N'dbo.Customers', N'U') IS NULL
CREATE TABLE dbo.Customers (
    CustomerId INT IDENTITY(1,1) PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    Phone VARCHAR(20) NULL,
    Address NVARCHAR(255) NULL
);
GO

IF OBJECT_ID(N'dbo.Orders', N'U') IS NULL
CREATE TABLE dbo.Orders (
    OrderId INT IDENTITY(1,1) PRIMARY KEY,
    CustomerId INT NULL, -- NULL: walk-in customer
    OrderDate DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    Status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    Note NVARCHAR(500) NULL,
    CONSTRAINT CK_Orders_Status CHECK (Status IN ('NEW','CONFIRMED','CANCELLED')),
    CONSTRAINT FK_Orders_Customers FOREIGN KEY (CustomerId)
        REFERENCES dbo.Customers(CustomerId)
);
GO

IF OBJECT_ID(N'dbo.OrderItems', N'U') IS NULL
CREATE TABLE dbo.OrderItems (
    OrderId INT NOT NULL,
    ProductId INT NOT NULL,
    Quantity INT NOT NULL CHECK (Quantity > 0),
    UnitPrice DECIMAL(12,2) NOT NULL CHECK (UnitPrice >= 0),
    CONSTRAINT PK_OrderItems PRIMARY KEY (OrderId, ProductId),
    CONSTRAINT FK_OrderItems_Orders FOREIGN KEY (OrderId)
        REFERENCES dbo.Orders(OrderId),
    CONSTRAINT FK_OrderItems_Products FOREIGN KEY (ProductId)
        REFERENCES dbo.Products(ProductId)
);
GO

-- Seed data runs only when corresponding tables are empty.
IF NOT EXISTS (SELECT 1 FROM dbo.Categories)
    INSERT INTO dbo.Categories (Name) VALUES
    (N'Đồ uống'), (N'Đồ ăn nhẹ'), (N'Đồ dùng cá nhân');

IF NOT EXISTS (SELECT 1 FROM dbo.Products)
    INSERT INTO dbo.Products (CategoryId, Name, Price, Stock, ImageUrl) VALUES
    (1, N'Nước suối 500ml', 7000, 100, NULL),
    (1, N'Trà đào chai', 15000, 50, NULL),
    (1, N'Cà phê lon', 18000, 40, NULL),
    (2, N'Bánh quy bơ', 25000, 30, NULL),
    (2, N'Mì ly', 12000, 60, NULL),
    (3, N'Khăn giấy', 10000, 80, NULL);

IF NOT EXISTS (SELECT 1 FROM dbo.Customers)
    INSERT INTO dbo.Customers (FullName, Phone, Address) VALUES
    (N'Nguyễn Minh Nhật', '0900000001', N'TP. Hồ Chí Minh'),
    (N'Trần Văn An', '0900000002', N'TP. Hồ Chí Minh');
GO

-- Example product-list query
SELECT p.ProductId, p.Name, c.Name AS CategoryName,
       p.Price, p.Stock, p.ImageUrl
FROM dbo.Products p
JOIN dbo.Categories c ON c.CategoryId = p.CategoryId
WHERE p.IsActive = 1
ORDER BY p.ProductId;
GO

-- Example order totals: UnitPrice is stored at sale time
SELECT o.OrderId, o.OrderDate, o.Status,
       COALESCE(c.FullName, N'Khách vãng lai') AS CustomerName,
       SUM(oi.Quantity * oi.UnitPrice) AS TotalAmount
FROM dbo.Orders o
LEFT JOIN dbo.Customers c ON c.CustomerId = o.CustomerId
JOIN dbo.OrderItems oi ON oi.OrderId = o.OrderId
GROUP BY o.OrderId, o.OrderDate, o.Status, c.FullName
ORDER BY o.OrderId DESC;
GO
