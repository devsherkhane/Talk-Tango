package com.example.talktango2

class AnonymousUser {
    var anonymousId: String? = null
    var displayName: String? = null
    
    constructor() {}
    
    constructor(anonymousId: String?, displayName: String?) {
        this.anonymousId = anonymousId
        this.displayName = displayName
    }
} 
