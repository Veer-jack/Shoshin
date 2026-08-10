package com.Shoshin.app.data.routine

import com.Shoshin.app.R

data class RoutineCheckpointDef(
    val label: String,
    val icon: Int,
    val type: String,
    val targets: List<String> = emptyList(),
    val hintPool: List<String> = emptyList(),
    val description: String = ""
)

object RoutineDefinitions {
    val TEMPLATE_CHECKPOINTS: Map<String, List<RoutineCheckpointDef>> = mapOf(
        "walk" to listOf(
            RoutineCheckpointDef("Mind awake", R.drawable.ic_brain, "math", description = "Solve three patterns to bridge the dream state to the day."),
            RoutineCheckpointDef(
                "Freshen up", R.drawable.ic_droplet, "photo",
                listOf("Sink", "Toothbrush", "Bathroom", "Mirror", "Water", "Tap", "Faucet"),
                listOf("Photo of sink or toothbrush", "Photo of mirror or washbasin", "Photo of towel or soap", "Photo showing water or sink"),
                "Cold water resets the system. Thirty seconds is enough."
            ),
            RoutineCheckpointDef(
                "Dressed", R.drawable.ic_shirt, "photo",
                listOf("Person", "Clothing", "Selfie", "Fashion"),
                listOf("Selfie in your gear", "Photo of your outfit", "Photo showing you're dressed", "Selfie ready to move"),
                "Armor for the morning. Ready to move."
            ),
            RoutineCheckpointDef(
                "Out the door", R.drawable.ic_sun, "photo",
                listOf("Tree", "Street", "Sky", "Building", "Outdoor", "Plant"),
                listOf("Photo of a tree or street", "Photo of the sky outside", "Photo of your doorway", "Photo showing you're outside"),
                "The threshold is the hardest part. Cross it now."
            ),
            RoutineCheckpointDef(
                "Walk begun", R.drawable.ic_walk, "photo",
                listOf("Person", "Outdoor", "Street", "Walking", "Shoe", "Road", "Path"),
                listOf("Photo of your shoes on the path", "Photo of the road ahead", "Photo showing you're walking", "Selfie mid-walk"),
                "The path is open. Walk for at least 15 minutes."
            )
        ),
        "study" to listOf(
            RoutineCheckpointDef("Mind awake", R.drawable.ic_brain, "math", description = "Sharpen the focus with a quick mental warmup."),
            RoutineCheckpointDef(
                "Freshen up", R.drawable.ic_droplet, "photo",
                listOf("Sink", "Toothbrush", "Water tap", "Bathroom"),
                listOf("Photo of sink", "Photo of mirror or washbasin", "Photo of towel or soap", "Photo showing water or sink"),
                "Awaken the senses with cold water."
            ),
            RoutineCheckpointDef(
                "Tea brewed", R.drawable.ic_check, "photo",
                listOf("Cup", "Mug", "Tea", "Drink", "Coffee"),
                listOf("Photo of your tea", "Photo of your cup or mug", "Photo of your drink brewing", "Photo showing your warm drink"),
                "A warm ritual to settle into deep work."
            ),
            RoutineCheckpointDef(
                "Desk ready", R.drawable.ic_check, "photo",
                listOf("Book", "Laptop", "Computer", "Paper", "Desk", "Writing", "Office"),
                listOf("Photo of your desk", "Photo of your laptop or book", "Photo of your workspace", "Photo showing you're set up"),
                "Clear space, clear mind. The work begins."
            ),
            RoutineCheckpointDef(
                "Study begun", R.drawable.ic_book, "photo",
                listOf("Book", "Laptop", "Paper", "Desk", "Person", "Writing"),
                listOf("Photo of your open book or laptop", "Photo of you at your desk, working", "Photo of your notes or screen", "Selfie mid-study"),
                "Enter the deep state. Keep the door closed."
            )
        ),
        "gym" to listOf(
            RoutineCheckpointDef("Mind awake", R.drawable.ic_brain, "math", description = "Solve three patterns to bridge the dream state to the day."),
            RoutineCheckpointDef(
                "Freshen up", R.drawable.ic_droplet, "photo",
                listOf("Sink", "Toothbrush", "Bathroom", "Mirror", "Water", "Tap", "Faucet"),
                listOf("Photo of sink or toothbrush", "Photo of mirror or washbasin", "Photo of towel or soap", "Photo showing water or sink"),
                "Cold water resets the system. Thirty seconds is enough."
            ),
            RoutineCheckpointDef(
                "Kit on", R.drawable.ic_shirt, "photo",
                listOf("Clothing", "Shoe", "Person", "Selfie", "Sportswear"),
                listOf("Selfie in your workout gear", "Photo of your gym kit on", "Photo of your sneakers", "Photo showing you're dressed to train"),
                "Gear on, mind set. Ready to train."
            ),
            RoutineCheckpointDef(
                "Gym reached", R.drawable.ic_dumbbell, "photo",
                listOf("Gym", "Building", "Weights", "Equipment", "Fitness", "Room"),
                listOf("Photo of the gym entrance", "Photo of the equipment", "Photo showing you've arrived", "Photo of the gym floor"),
                "The threshold is the hardest part. Cross it now."
            ),
            RoutineCheckpointDef(
                "Workout begun", R.drawable.ic_dumbbell, "photo",
                listOf("Gym", "Equipment", "Person", "Weights", "Exercise"),
                listOf("Photo starting your first set", "Selfie before you begin", "Photo of the machine or mat", "Photo showing you're underway"),
                "The work begins. Train for at least 20 minutes."
            )
        )
    )

    fun forTemplate(key: String): List<RoutineCheckpointDef> =
        TEMPLATE_CHECKPOINTS[key] ?: TEMPLATE_CHECKPOINTS["walk"]!!
}
