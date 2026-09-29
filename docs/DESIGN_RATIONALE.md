# Design rationale — MatchCommentary

**Complexity module chosen: Dynamic implementor selection** (section 5, option 1).

## 1. Problem and domain
A live football-match commentary system. *What* is said about the match (a commentary style) and *where* the line is delivered (an output channel) are independent decisions.
- Abstraction hierarchy: `MatchCommentator` → `BriefCommentator`, `DetailedCommentator`, `AccessibleCommentator` (for listeners who cannot see the screen: announces minute and running score every time).
- Implementor hierarchy: `CommentaryOutput` → `ConsoleOutput`, `SubtitleFileOutput`, `SpeechOutput` (adapter).

## 2. Why Bridge alone is not enough
Bridge assumes each implementor already fits `CommentaryOutput`. The only speech engine available, `LegacySpeechEngine`, is a vendor library we cannot edit and it does not fit. Without an adapter, the abstraction would have to contain vendor speed values, language codes, status-code checks and text splitting, so it would stop depending only on the Implementor interface.

## 3. Why Adapter alone is not enough
An adapter would make speech usable by one commentator, but with 3 styles × 3 channels we would still write 9 classes (`BriefSpeechCommentator`, `DetailedFileCommentator`, …). Bridge reduces this to 3 + 3 and lets styles and channels vary independently.

## 4. Why `LegacySpeechEngine` is genuinely incompatible
| Aspect | `CommentaryOutput` | `LegacySpeechEngine` |
|---|---|---|
| Method | `emit(String text, Tone tone)` | `speak(int wpm, int langCode, String utterance)` |
| Argument order / types | text first, `Tone` enum | speed first, numeric language code |
| Input constraint | any length | refuses > 120 chars |
| Logical failure | checked `OutputException` subtypes | `SpeechStatus` object with numeric codes (5, 7, 9) |
| Hardware failure | `OutputUnavailableException` | unchecked `AudioDeviceException` |

`SpeechOutput` converts `Tone`→words-per-minute, `Locale`→language code, splits long text on word boundaries, and maps the status object, an unknown or null status and the vendor exception to the `OutputException` family. No vendor type appears in `kz.commentary.commentator` or in the `CommentaryOutput` signature.

## 5. Dynamic implementor selection
`OutputResolver` picks the implementor from the scheme of a target string at runtime (`console:`, `srt:///path`, `speech://en`); `CommentatorResolver` does the same for the style. `Main` is the only class that names concrete types.

## 6. Open/Closed on both axes
- New abstraction: `AccessibleCommentator` was added as a subclass; no existing class was edited.
- New implementor: register a new scheme (see `DynamicSelectionTest.newOutputCanBeAddedWithoutChangingExistingClasses`).

## 7. Limitation
`SpeechOutput` treats `Tone` as speaking speed only, and chooses the language once at construction. A commentator that wants to switch language mid-match, or use vendor voice effects, cannot express that through the `emit(text, tone)` contract without widening the Implementor interface for every other output too. Also, a failure in a multi-part utterance can leave the earlier parts already spoken.
