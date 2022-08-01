package com.education4all.firebase

import com.education4all.mathCoachAlg.tours.Tour

class User {
    var email: String? = null
    var id: String? = null
    var statistics: ArrayList<Tour>? = null

    constructor() {}
    constructor(name: String?, email: String?, id: String?, statistics: ArrayList<Tour>?) {
        this.email = email
        this.id = id
        this.statistics = statistics
    }
}