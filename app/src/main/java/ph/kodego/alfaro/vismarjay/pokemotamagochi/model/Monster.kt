package ph.kodego.alfaro.vismarjay.pokemotamagochi.model


data class Monster(
    val id: Int = 0,
    val image: String,
    val name: String,
    val description: String,
    var hunger: Int,
    var happiness: Int,
    var health: Int,
    var lastFedTime: Long,
    var lastPlayedTime: Long,
    var lastSleptTime: Long,
    var isSleeping: Boolean = false
) {
    // Decrease hunger, happiness, and health over time
    fun decreaseStats() {
        hunger -= 5
        happiness -= 5
        health -= 5
    }

    // Increase happiness when played with
    fun play() {
        happiness += 10
        if (happiness > 100) happiness = 100
        lastPlayedTime = System.currentTimeMillis()
    }

    // Feed the monster, increasing hunger and health
    fun feed() {
        hunger += 20
        if (hunger > 100) hunger = 100
        health += 10
        if (health > 100) health = 100
        lastFedTime = System.currentTimeMillis()
    }

    // Put the monster to sleep, increasing health and resetting hunger and happiness
    fun sleep() {
        isSleeping = true
        health += 10
        if (health > 100) health = 100
        hunger = 0
        happiness = 0
        lastSleptTime = System.currentTimeMillis()
    }

    // Wake up the monster, resetting health and happiness
    fun wakeUp() {
        isSleeping = false
        health = 100
        happiness = 50
    }

    // Check if the monster is hungry
    fun isHungry(): Boolean {
        return hunger <= 25
    }

    // Check if the monster is unhappy
    fun isUnhappy(): Boolean {
        return happiness <= 25
    }

    // Check if the monster is sick
    fun isSick(): Boolean {
        return health <= 25
    }

    // Heal the monster, increasing health
    fun heal() {
        health += 20
        if (health > 100) health = 100
    }

    // Train the monster, increasing health and happiness
    fun train() {
        health += 10
        if (health > 100) health = 100
        happiness += 10
        if (happiness > 100) happiness = 100
    }
}
