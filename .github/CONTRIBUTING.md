## Setting up project

### 1.12 and up

should just work in intellij idea.
builds with `./gradlew build`, outputs are in `./dist` folder

### 1.8.9

additional step of
`./gradlew setupdevworkspace`
and an extra refresh afterward.

### notes

forge sometimes doesn't like working in dev...

## PR Guidelines

This is mainly for my sanity and so that your PRs can be merged faster.

* try not to mess with the style too much, it can make backporting more difficult
  * if you *only* change the style in a file, DONT.
* target the current main branch
* split PRs up into managable sections by feature
  * I don't want to see PR like [#71](https://github.com/JsMacros/JsMacros/pull/71) where there's a ton of different stuff in it.
  * PRs should be for a **SINGLE** feature, or related group of features (ie. a new library)
