package com.education4all.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Enums {
    fun isSubsequent(prevdate: String?, date: String?): twodates {
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val c1 = Calendar.getInstance()
        val c2 = Calendar.getInstance()
        try {
            c1.time = sdf.parse(prevdate)
            c2.time = sdf.parse(date)
            if (c1 == c2) return twodates.equal
            c1.add(Calendar.DATE, 1)
            if (c1 == c2) return twodates.subsequent
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return twodates.unrelated
    }

    enum class TimerState {
        CONTINIOUS, DISCRETE, INVISIBlE;

        companion object {
            fun convert(i: Int): TimerState {
                return when (i) {
                    0 -> CONTINIOUS
                    1 -> DISCRETE
                    2 -> INVISIBlE
                    else -> CONTINIOUS
                }
            }

            fun convert(ts: TimerState): Int {
                return ts.ordinal
            }
        }
    }

    enum class ButtonsPlace {
        RIGHT, LEFT;

        companion object {
            fun convert(i: Int): ButtonsPlace {
                return when (i) {
                    0 -> RIGHT
                    1 -> LEFT
                    else -> RIGHT
                }
            }

            fun convert(bp: ButtonsPlace): Int {
                return bp.ordinal
            }
        }
    }

    enum class LayoutState {
        _789, _123;

        companion object {
            fun convert(i: Int): LayoutState {
                return when (i) {
                    0 -> _789
                    1 -> _123
                    else -> _789
                }
            }

            fun convert(ls: LayoutState): Int {
                return ls.ordinal
            }
        }
    }

    enum class Theme {
        DARK, LIGHT, SYSTEM;

        companion object {
            fun convert(i: Int): Theme {
                return when (i) {
                    0 -> DARK
                    1 -> LIGHT
                    2 -> SYSTEM
                    else -> DARK
                }
            }

            fun convert(t: Theme): Int {
                return t.ordinal
            }
        }
    }

    enum class twodates {
        equal, subsequent, unrelated
    }
}