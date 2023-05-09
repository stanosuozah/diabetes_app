package com.jhealth.diabetesapp.data.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.jhealth.diabetesapp.domain.model.Recipe
import com.jhealth.diabetesapp.util.isNotNullOrEmptyAction
import kotlinx.parcelize.Parcelize

@Parcelize
data class FullMealDTO(
    @SerializedName("idMeal")
    val mealId: String,
    @SerializedName("strArea")
    val strArea: String,
    @SerializedName("strCategory")
    val mealCategory: String,
    @SerializedName("strIngredient1")
    val ingredient1: String?,
    @SerializedName("strIngredient2")
    val ingredient2: String?,
    @SerializedName("strIngredient3")
    val ingredient3: String?,
    @SerializedName("strIngredient4")
    val ingredient4: String?,
    @SerializedName("strIngredient5")
    val ingredient5: String?,
    @SerializedName("strIngredient6")
    val ingredient6: String?,
    @SerializedName("strIngredient7")
    val ingredient7: String?,
    @SerializedName("strIngredient8")
    val ingredient8: String?,
    @SerializedName("strIngredient9")
    val ingredient9: String?,
    @SerializedName("strIngredient10")
    val ingredient10: String?,
    @SerializedName("strIngredient11")
    val ingredient11: String?,
    @SerializedName("strIngredient12")
    val ingredient12: String?,
    @SerializedName("strIngredient13")
    val ingredient13: String?,
    @SerializedName("strIngredient14")
    val ingredient14: String?,
    @SerializedName("strIngredient15")
    val ingredient15: String?,
    @SerializedName("strIngredient16")
    val ingredient16: String?,
    @SerializedName("strIngredient17")
    val ingredient17: String?,
    @SerializedName("strIngredient18")
    val ingredient18: String?,
     @SerializedName("strIngredient19")
    val ingredient19: String?,
     @SerializedName("strIngredient20")
    val ingredient20: String?,
    @SerializedName("strInstructions")
    val instructions: String,
    @SerializedName("strMeal")
    val mealTitle: String,
    @SerializedName("strMealThumb")
    val mealImg: String,
    @SerializedName("strMeasure1")
    val measure1: String?,
    @SerializedName("strMeasure2")
    val measure2: String?,
    @SerializedName("strMeasure3")
    val measure3: String?,
    @SerializedName("strMeasure4")
    val measure4: String?,
    @SerializedName("strMeasure5")
    val measure5: String?,
    @SerializedName("strMeasure6")
    val measure6: String?,
    @SerializedName("strMeasure7")
    val measure7: String?,
    @SerializedName("strMeasure8")
    val measure8: String?,
    @SerializedName("strMeasure9")
    val measure9: String?,
    @SerializedName("strMeasure10")
    val measure10: String?,
    @SerializedName("strMeasure11")
    val measure11: String?,
    @SerializedName("strMeasure12")
    val measure12: String?,
    @SerializedName("strMeasure13")
    val measure13: String?,
    @SerializedName("strMeasure14")
    val measure14: String?,
    @SerializedName("strMeasure15")
    val measure15: String?,
    @SerializedName("strMeasure16")
    val measure16: String?,
    @SerializedName("strMeasure17")
    val measure17: String?,
    @SerializedName("strMeasure18")
    val measure18: String?,
    @SerializedName("strMeasure19")
    val measure19: String?,
    @SerializedName("strMeasure20")
    val measure20: String?,
    @SerializedName("strTags")
    val strTags: String,
    @SerializedName("strYoutube")
    val strYoutube: String
) : Parcelable{

    fun toRecipe(): Recipe {
        val stringBuilder = StringBuilder()

        ingredient1.isNotNullOrEmptyAction {
            stringBuilder.append(" 1. $it: $measure1")
        }
        ingredient2.isNotNullOrEmptyAction {
            stringBuilder.append("\n 2. $it: $measure2")
        }
        ingredient3.isNotNullOrEmptyAction {
            stringBuilder.append("\n 3. $it: $measure3")
        }
        ingredient4.isNotNullOrEmptyAction {
            stringBuilder.append("\n 4. $it: $measure4")
        }
        ingredient5.isNotNullOrEmptyAction {
            stringBuilder.append("\n 5. $it: $measure5")
        }
        ingredient6.isNotNullOrEmptyAction {
            stringBuilder.append("\n 6. $it: $measure6")
        }
        ingredient7.isNotNullOrEmptyAction {
            stringBuilder.append("\n 7. $it: $measure7")
        }
        ingredient8.isNotNullOrEmptyAction {
            stringBuilder.append("\n 8. $it: $measure8")
        }
        ingredient9.isNotNullOrEmptyAction {
            stringBuilder.append("\n 9. $it: $measure9")
        }
        ingredient10.isNotNullOrEmptyAction {
            stringBuilder.append("\n 10. $it: $measure10")
        }
        ingredient11.isNotNullOrEmptyAction {
            stringBuilder.append("\n 11. $it: $measure11")
        }
        ingredient12.isNotNullOrEmptyAction {
            stringBuilder.append("\n 12. $it: $measure12")
        }
        ingredient13.isNotNullOrEmptyAction {
            stringBuilder.append("\n 13. $it: $measure13")
        }
        ingredient14.isNotNullOrEmptyAction {
            stringBuilder.append("\n 14. $it: $measure14")
        }
        ingredient15.isNotNullOrEmptyAction {
            stringBuilder.append("\n 15. $it: $measure15")
        }
        ingredient16.isNotNullOrEmptyAction {
            stringBuilder.append("\n 16. $it: $measure16")
        }
        ingredient17.isNotNullOrEmptyAction {
            stringBuilder.append("\n 17. $it: $measure17")
        }
        ingredient18.isNotNullOrEmptyAction {
            stringBuilder.append("\n 18. $it: $measure18")
        }
        ingredient19.isNotNullOrEmptyAction {
            stringBuilder.append("\n 19. $it: $measure19")
        }
        ingredient20.isNotNullOrEmptyAction {
            stringBuilder.append("\n 20. $it: $measure20")
        }

        return Recipe(
            id = mealId.toInt(),
            recipe_title = mealTitle,
            recipe_author = "TheMealDB.com",
            recipe_category = mealCategory,
            recipe_image = mealImg,
            recipe_video = strYoutube,
            nutrition_fact = "Delicious meal",
            cooking_instruction = instructions,
            ingredient = stringBuilder.toString()
        )
    }

}