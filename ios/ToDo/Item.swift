//
//  Item.swift
//  ToDo
//
//  Created by Maximilian Alexander on 1/16/18.
//  Copyright © 2018 Maximilian Alexander. All rights reserved.
//

import RealmSwift

class Item: Object {
    
    @objc dynamic var itemId: String = UUID().uuidString
    @objc dynamic var body: String = ""
    @objc dynamic var isDone: Bool = false
    @objc dynamic var timestamp: Date = Date()
    
}
