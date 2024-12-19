package com.kennycason.struktural

import org.junit.Test

class JsonTypeAssertionTest {

    @Test
    fun `assert complex Super Metroid json`() {
        val json = """
        {
            "game_title": "Super Metroid",
            "release_year": 1994,
            "characters": [
                {
                    "name": "Samus Aran",
                    "abilities": ["Morph Ball", "Missiles", "Power Bombs"],
                    "health": 99,
                    "weapon_power": 100.5
                },
                {
                    "name": "Ridley",
                    "abilities": ["Fire Breath", "Tail Whip"],
                    "health": 5000,
                    "weapon_power": 200.0
                }
            ],
            "settings": {
                "planet": "Zebes",
                "areas": [
                    {
                        "name": "Brinstar",
                        "environment": "Jungle"
                    },
                    {
                        "name": "Norfair",
                        "environment": "Lava"
                    }
                ]
            },
            "game_modes": ["SINGLE_PLAYER"],
            "game_mode": "SINGLE_PLAYER",
            "is_remake": null
        }
        """

        assertJsonTypes(json) {
            string("game_title")
            integer("release_year")
            nullableBoolean("is_remake")

            array("characters") {
                string("name")
                array("abilities") {
                    string("")
                }
                integer("health")
                decimal("weapon_power")
            }

            objectField("settings") {
                string("planet")
                array("areas") {
                    string("name")
                    string("environment")
                }
            }

            array("game_modes") { }

            custom("release_year", "a valid SNES release year") {
                it.isInt && it.asInt() in 1990..2000
            }

            enum("game_mode") { arrayOf("SINGLE_PLAYER", "MULTIPLAYER", "CO-OP") }
        }
    }

    @Test
    fun `assert types`() {
        val json = """
        {
            "languages": [
                {
                    "name": "kotlin",
                    "coolness": 100
                },
                {
                    "name": "java",
                    "coolness": 50
                }
            ],
            "details": {
                "author": "kenny",
                "year": 2024
            }
        }
        """

        assertJsonTypes(json) {
            array("languages") {
                string("name")
                number("coolness")
            }
            objectField("details") {
                string("author")
                number("year")
            }
        }
    }

    @Test
    fun `validate date fields`() {
        val json = """
        {
            "game_release_date": "1994-03-19",
            "last_updated": "2024-12-18T15:30:00"
        }
        """

        assertJsonTypes(json) {
            date("game_release_date", "yyyy-MM-dd")
            dateTime("last_updated", "yyyy-MM-dd'T'HH:mm:ss")
        }
    }

    @Test
    fun `validate regex fields`() {
        val json = """
        {
            "player_name": "Samus_Aran",
            "player_code": "SM12345"
        }
        """

        assertJsonTypes(json) {
            matchesRegex("player_name", "^[a-zA-Z0-9_]+$".toRegex()) // Alphanumeric with underscores
            matchesRegex("player_code", "^SM[0-9]{5}$".toRegex()) // Starts with 'SM' followed by 5 digits
        }
    }

}
