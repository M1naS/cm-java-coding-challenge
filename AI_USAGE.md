## AI Usage
This is a file that describes how AI was used in the app
### 1. Tool Used
#### Google Gemini
It was used as an assistant, in place of searching the web
- Aggregation of information
- Questions about business
  - ``What is SDMX?``
  -  ``When is the time at which the new rates for Bundesbank Daily Exchange Rates inputs a new day?``
  - etc.
- Help with package names
- Help with filenames
- Help with commits
- Some snippets were used but were understood and changed to fit my vision.
- Used as a technical reference.

**No Coding assistant was used (Gemini CLI, Claude Code, etc)**

### 2. Key prompts/Questions asked
- #### Getting the API link, which at first was wrong and then right
- #### Made sure that there are no updates to historical data
- #### Asked when was the data updated (New exchange data is written to API)
- #### Asked which of the ``currencies`` from my first API are logical, groups, etc
- #### Which rounding for the currency is best for ``EUR``
- #### Impact of certain ideas on performance
- #### Best library for dealing with ``csv`` files

### 3. Relevant AI responses that shaped your solution
- #### API link was used with ``bruno`` after I got its `yml` file
- #### Cached data on startup
- #### Updated data around the time that it is updated, daily except on weekends
- #### Filtered the logical and groups out of the extra API I returned
- #### It was ``RoundingMode.HALF_UP`` and a scale ``2``, I found out it differs but since all the results are to `EUR` it does not matter
- #### For example, I wanted to Cache in startup, so there was a performance concern
- #### There were some options but I have already worked with ``Jackson`` and choose it for convenience

### 4. Your reasoning for accepting, modifying, or rejecting AI suggestions
- #### I accept them if I already have all the information that confirms its response
- #### I modify them if I am not certain and try them out and then reach a conclusion based on the result
  - #### For example: I thought of directly caching the data to the cache while reading them on startup, instead of ``adding`` them to the cache after the data is returned
    - The AI mentioned it was the perfect idea but I tested both and the results were a few seconds that did not matter that much as I'll have to handle caching in a place that I shouldn't
- #### I reject them when there is no gain, they would need multiple dependencies to work or that they would only complicate the code