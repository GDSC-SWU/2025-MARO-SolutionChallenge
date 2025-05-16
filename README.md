# 2025\_MARO\_SolutionChallenge
## 📑 Table of Contents

* [👥 Introduction](#-introduction)
* [❗ Problem Statement](#-problem-statement)
* [💡 Solution Overview: MARO](#-solution-overview-maro)
* [🎯 UN SDGs Alignment](#-un-sdgs-alignment)
* [⚙️ How to Run](#-how-to-run)
* [🎥 Demo Video](#-demo-video)
* [🧱 Technology Stack](#-technology-stack)
  * [✅ Server](#-server)
  * [✅ Android](#-android)
  * [✅ AI](#-ai)
* [✅ Privacy Policy](#-privacy-policy)
* [📱 Prototype Screens](#-prototype-screens)
* [🌱 Expected Effects](#-expected-effects)
* [🔚 Closing](#-closing)
* [👩🏻‍⚕️Contributors](#%EF%B8%8Fcontributors)

  
---

 

## 👥 Introduction
![Image](https://github.com/user-attachments/assets/42bd088c-e8dd-4ccc-ad55-44a78fa6aa0d)

**MARO (Multicultural + Aid + Routine + Organizer)** is a **vaccination information integration management app** designed for multicultural families who face difficulties with vaccinations due to language barriers and lack of information.  
It is a **customized vaccine care solution for multicultural families**, providing everything from child-specific schedules to document translation, adverse reaction response, and policy information.


---


## ❗ Problem Statement

![image](https://github.com/user-attachments/assets/9339b419-926d-464c-bf78-afbb522370c1)
[> 📊 Source: Statistics Korea Population Census, 2023](https://kosis.kr/visual/populationKorea/PopulationDashBoardDetail.do?statJipyoId=3789&vStatJipyoId=4873&listId=A_02&areaId=&areaNm=)

### 🔍 Background

The number of **multicultural households in South Korea has been steadily increasing**, from **299,241 households in 2015 to 415,584 households in 2023**, an increase of approximately **over 39%**.

This goes beyond simple cultural diversity and indicates that **the health management of children in multicultural families is emerging as a public health issue for the entire society.**

### ⚠️ Key Issues

#### 1. Vaccination gaps due to language and cultural barriers

- Most child vaccination information is **provided only in Korean**
- Guardians often face the following situations:
  - **Missing vaccination schedules**
  - **Lack of awareness of vaccination history**
  - **Giving up on vaccinations due to anxiety**

#### 2. Vaccination is a critical means of saving lives

- According to WHO, **about 3 million lives can be saved each year through vaccination**
- Missing vaccinations can lead to **exposure to fatal diseases** such as:
  - **Measles, diphtheria, pertussis, etc.**
- **Lack of information on adverse reactions** makes prompt response difficult, possibly resulting in **serious health damage**

#### 3. Information gap in existing systems

- Medical documents, vaccination schedules, and hospital information are **provided only in non-standardized Korean**
- Multicultural guardians have **limited access to accurate and reliable vaccine information**
- **Multilingual pre-vaccination forms are partially supported**, but in actual vaccination apps, **pre-filling is only available in Korean and English**, and **most public health centers provide forms only in Korean**, making it **difficult for multicultural guardians to fill them out independently**
- **Without support from local multicultural centers**, guardians **struggle to make vaccination reservations and manage full schedules on their own**

### 📚 Prior Research and Policy Implications

#### 1. High demand for maternal and child health services among marriage migrant women

- Many marriage migrant women in Korea **experience pregnancy and childbirth before acquiring citizenship**
- They constitute a group with a **particularly high demand** for:
  - **Maternal and child health services**
  - **Vaccinations**
  - **Mental health support**

#### 2. Language barriers in accessing healthcare

- The Korean healthcare system requires **specialized knowledge**, making it difficult for migrants to navigate
- **Language barriers prevent them from understanding medical information and using healthcare services**
- Interviews with multicultural mothers reveal:
  - _“When my baby is sick, I don’t know how to explain it to the doctor, so I always have to go with my husband.”_
  - _“Even vaccination schedules—my husband has to manage them. It’s too difficult to handle alone.”_

#### 3. Policy research supports information-based solutions

- According to prior research:
  
  > “The main policy beneficiaries are marriage migrant women.  
  > If the policy goals are set as maternal and child health, vaccination, and mental health services,  
  > and the policy tool is ‘information provision,’  
  > the policy outcomes are expected to be significantly effective.”  
  > — *Policies and ICT Strategies Based on Health Needs for Multicultural Families, Suyong Jeong et al.*

- This emphasizes the need for:
  - A **multilingual information delivery system**
  - A **user-friendly digital platform** that supports autonomous health management by multicultural families


---


## 💡 Solution Overview: MARO

**MARO** is an integrated vaccination platform for multicultural families that improves information accessibility and medical responsiveness through multilingual support, location-based information, and AI features such as voice and image recognition.

### 🚀 MVP

#### 1️⃣ Multilingual Support

* Supports key languages such as Korean, Vietnamese, Chinese, and Thai
* Allows guardians to communicate smoothly with hospitals using translated sentences and pronunciations

#### 2️⃣ Child-Specific Vaccination Schedule

* Automatically generates and manages personalized schedules based on each child's information

#### 3️⃣ Location-Based Vaccination Center Guide

* Provides information on nearby hospitals/public health centers based on user location
* Offers detailed institution information with translation

#### 4️⃣ CallScript Feature

* Generates expected Q&A sentences and keywords before phone calls
* Enables practice through speech conversion

#### 5️⃣ Vaccination Chatbot (RAG-based)

* Provides consultation on vaccination information and multicultural policies
* Determines whether a hospital visit is necessary when adverse reaction images are uploaded

#### 6️⃣ Vaccination History & Adverse Reaction Records

* Automatically recognizes vaccination certificate images and updates records
* Records post-vaccination symptoms for hospital consultations

### Differentiation from Existing Methods

| Category | Existing Vaccination Apps | MARO |
|---------|----------------------------|------|
| Language Support | Korean, English | Korean, English, Chinese, Japanese, Vietnamese, Thai |
| Schedule Management | Based on standard tables | Generates personalized schedules per child |
| Hospital Information | Simple map info | Location-based + detailed info + translation |
| Chatbot | None or simple Q&A | RAG-based, reliable policy/vaccine responses + image analysis |



---


## 🎯 UN SDGs Alignment

### 🌐 Related Goals

<table>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/eeaf22f1-e900-4e59-8247-e633e49aff20" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/34ba43ab-de84-4c19-a81e-deb1bd9ae741" width="400"/></td>
    <td><img src="https://github.com/user-attachments/assets/3d3b7a33-c87f-4373-924b-b5cf3bbecbac" width="400"/></td>
  </tr>
</table>

### 📌 Detailed Connection

In situations where healthcare infrastructure is not well established, no policy efforts can be truly effective. In particular, foreign guardians are often excluded from both vaccination information and access to medical services.

Accordingly, MARO offers **multilingual translation and voice-based AI (CallScript)** functions to overcome language barriers and is designed as a platform that integrates various auxiliary functions including **child-specific vaccination schedule management**, chatbot, image analysis, and certificate validation. This technological approach goes beyond simple information provision and is structured to positively impact the entire vaccination system.

Moreover, the AI is **ethically designed not to perform direct medical diagnosis**, and guides users to visit medical institutions when needed, thereby clarifying the scope of medical responsibility. MARO also considers future **scalability to connect not only with guardians but also with medical staff and hospital systems**, such as integration with hospital reservation systems.

> **MARO** is not just a chatbot service,  
> but a **reliable public healthcare platform** that combines various AI technologies such as voice, translation, and verification,  
> centered on **vaccination schedule management for children in multicultural families**.


---


## ⚙️ How to Run

1. Clone the project and run it in Android Studio  
2. Enter your Google API Key in the `.env` file  
3. Sync Gradle and build the app  

🔗 APK 다운로드: [링크 삽입 예정]



---


## 🎥 Demo Video

> 데모 영상 링크 또는 YouTube 삽입 예정


---


## 🧱 Technology Stack
![image](https://github.com/user-attachments/assets/69fa8940-822f-4380-93bd-cfc966f9f1e3)

### ✅ Server

#### **1. Main Features and Processing Flow**

**1) Nearby Hospitals Lookup**

- Retrieves a list of nearby hospitals based on user-input latitude and longitude
- Collects hospital location and basic info via Google Places API
- Periodically or on-demand loads a list of vaccination-available hospitals from the public data API and stores it in a cache (`vaccineHospitalMap`)
- Matches hospital names from Google Places with those in the cache using string normalization and Levenshtein distance-based similarity
- If vaccine info is matched, it is included in the hospital info; otherwise, a guide message is displayed
- Supports multilingual translation (Korean, English, etc.) for results

**2) Hospital Details Lookup**

- User requests detailed info with a hospital’s unique identifier (`placeId`)
- Calls the Google Place Details API to retrieve basic hospital info (address, phone number, business hours, etc.)
- Compares the retrieved hospital name with cached vaccine data
- If matching vaccine info exists, it is included in the details; otherwise, displays “No vaccine info” or “Please contact the hospital”
- Includes multilingual translation support based on request language

**3) Vaccine Info Cache Management**

- Periodically or on initialization retrieves hospital and vaccine info lists via public data API
- Stores retrieved data in internal cache for fast lookup and similarity-based hospital name matching
- Applies string normalization (lowercasing, removing whitespace) for consistency
- Uses Levenshtein distance-based matching to correct name discrepancies

**4) Multilingual Support and Translation**

- Automatically provides translations for key texts (hospital name, address, vaccine list) based on user's requested language
- Integrates translation clients like Google Translate API
- Target language can be specified as a parameter in API calls

**5) AI Natural Language Response and FastAPI Integration**

- Implements `FastApiClient` in Spring Boot
  - Sends HTTP requests to FastAPI server to call natural language response and speech generation functions
  - Merges or processes AI response data with hospital info before returning to client
- AI functionalities are handled by a separate FastAPI server to ensure clear role separation and enhance scalability and maintainability

**6) AI Chatbot Q&A API**

- Users send questions and language selection via `multipart/form-data`
- Integrates with AI server (e.g., FastAPI) to process natural language Q&A
- When images are attached, generates responses based on image recognition
- Key endpoints:
  - `/api/chat/free`: Text question response
  - `/api/chat/image`: Text + image question response

**7) Vaccine Certificate Verification API**

- Users upload vaccine name (in Korean/English), vaccination period, and certificate image to request authenticity verification
- Performs certificate verification through image processing and text matching
- Returns a Boolean value as the verification result
- Key endpoint:
  - `/api/vaccine/verify`

#### **2. Detailed Data Processing Flow**

- **Cache Initialization (on server start or periodic refresh)**
  - Calls public data API → Collects list of vaccination-available hospitals → Stores in internal cache

- **Nearby Hospitals Lookup**
  - User inputs coordinates → Calls Google Places API → Retrieves hospital list
  - Normalizes hospital names and compares with cache using similarity → Merges vaccine info → Translates and returns

- **Hospital Details Lookup**
  - Calls Google Place Details API with `placeId` → Retrieves basic hospital info
  - Looks up vaccine info in cache → Includes it or returns a guide message → Translates and returns

- **AI Natural Language Processing (FastAPI Integration)**
  - User sends AI query → Spring Boot’s `FastApiClient` calls FastAPI
  - Generates AI response → Merges in Spring Boot if needed and returns to client

- **AI Chatbot Q&A Processing**
  - User sends text or text+image via multipart/form-data
  - Spring Boot API integrates with AI server (FastAPI) to generate a response
  - Returns generated AI response to client

- **Vaccine Certificate Verification Processing**
  - User uploads vaccine name (Korean/English), vaccination period, and certificate image
  - Verifies authenticity based on image and input info
  - Returns verification result (true/false) to client

#### **3. Key Considerations and Advantages**

- **Cache Initialization and Refresh**: Stores public API data in cache at server start and periodically for faster queries
- **Normalization and Similarity Matching**: Uses lowercasing, whitespace removal, and Levenshtein distance to handle differences in hospital names (e.g., Korean/English, spacing, typos)
- **API Cost Optimization**: Uses internal cache instead of directly calling public data API during hospital detail lookups
- **Enhanced Error Handling**: Loads mock data and provides user guidance if public data API fails
- **Multilingual Support**: Supports various languages via integration with external translation APIs like Google Translate
- **AI Function Separation and Integration**: Handles AI natural language processing and TTS via a separate FastAPI server; Spring Boot acts as REST client, improving scalability and maintainability
- **Multipart Data Handling**: Supports complex data handling (image + text) and robust exception management during server-to-server API integration
- **Unified Response**: Returns AI response, hospital info, and vaccine verification results consistently and quickly to the client


---


### ✅ Android

To ensure accessibility and a high adoption rate for multicultural families living abroad, the app was developed **based on the Android platform**.

Android supports a wide range of language settings, runs well on low-spec devices, and provides easy integration with various APIs such as Google account authentication, Maps, and Calendar—making it ideal for this project.

### 🛠 Development Environment & Language

**Android Studio + Kotlin**

* Used Android Studio, the official IDE, to systematically handle UI design, debugging, and emulator testing, while maintaining a scalable project structure with Gradle-based dependency management.
* Kotlin's concise syntax and null-safety made it easy to write stable code, and its high compatibility with Android components such as Jetpack libraries and ViewBinding allowed efficient app development.

### 📦 Data Storage

**Room DB**

* Stores each child's vaccination schedule and basic information locally in a structured format, enabling quick retrieval and display across various screens (main list, detail view, etc.).
* Ideal for query operations such as filtering by vaccination status and sorting by vaccination date, ensuring a smooth and consistent user experience without server calls during app launch.
* Especially useful when managing multiple children, allowing each auto-generated vaccination list (based on birthdate) to be managed independently via local DB.

**SharedPreferences**

* Stores simple but frequently accessed data such as the selected language in key-value format, which is referenced for multilingual UI and server communication.
* Enables quick restoration of values even after app restarts and manages essential data efficiently.

### 🖼 UI Components

**RecyclerView**

* Used for efficiently displaying repeated elements such as vaccination lists per child and hospital lists.
* Handles various user interactions flexibly, including filtering, sorting, and item selection.

**ViewBinding**

* Allows safe access to views without using findViewById, improving code readability and stability.

**Google Map Fragment**

* Visualizes hospital location info on a map within the detail screen and enhances understanding with marker features.

**Navigation Bar Structure**

* Designed the bottom navigation bar for **quick and intuitive navigation** between key features (e.g., vaccination list, vaccination centers).
* Also used components such as BottomSheetDialog, FlexboxLayout chips, and MaterialCardView to improve information delivery and user experience.

### 🌐 Networking

**Retrofit + OkHttp + Coroutines**

* Used to connect with the AI server for features like the AI chatbot and vaccination certificate image analysis, as well as to reliably integrate public health data from KDCA and other external APIs.
* All network requests are structured via Retrofit; communication is monitored via OkHttp to handle errors gracefully.
* Time-consuming operations such as AI responses, translation results, and image analysis are processed **asynchronously with Coroutines** to maintain a smooth user experience.
* These server-based functions provide users with more accurate, multilingual vaccination information in real time.

### 🔐 Authentication

**Google OAuth**

* Implemented **easy login via Google accounts without a separate sign-up process**.
* Since most Android device users already have a Google account, this reduces the login barrier and simplifies the user experience.

### 📍 Location & Calendar API

**Google Maps API**

* Displays nearby vaccination hospitals on a map based on the user’s current location and shows markers in the hospital detail screen, with options to **open directions in a map app**.
* **Easy to integrate with Android Studio and well-supported by official documentation and libraries**, enabling quick and stable implementation of location-based features.

**Google Calendar API**

* Allows users to **add vaccination schedules directly to their Google Calendar**, enabling reminder notifications and schedule management.
* Easily integratable in Android without requiring backend linkage, offering a seamless experience with the user’s calendar app.

### 🌎 Localization (Multilingual Support)

* As the app targets multicultural families, multilingual UI support is one of the core features.
* All text elements are separated into multilingual resource files, automatically switching based on the selected language.
* The chatbot and hospital location information also include language codes in server requests to receive translated data.


---


### ✅ AI

#### 📞 **CallScript Feature (AI Hospital Call Practice Assistant)**

This feature is an AI-powered response system designed to allow **guardians in multicultural families to practice phone calls to hospitals** through both voice and text before making an actual call.

The user's question is automatically translated, and **realistic Korean responses that a hospital receptionist might give** are generated, then converted into audio. This helps reduce anxiety related to language barriers and enables **pre-call practice**.

**📌 Model & Tech Stack**

| Item               | Description                                                   |
|--------------------|---------------------------------------------------------------|
| **LLM Model**       | `gemini-1.5-flash` (generates text responses)                 |
| **Multimodal Model**| `gemini-2.0-flash-live-001` (generates TTS voice output)      |
| **Translation**     | `google-cloud-translate` (language detection & multilingual translation) |
| **Env Management**  | `dotenv`                                                      |
| **Other Tech**      | `wave`, `asyncio`, `contextlib`, `base64`, `json`, `re`, etc. |

**🎯 Feature Description**

1. **User Question Processing**

   * The user’s input is automatically **translated into Korean** using Google Translate API.

2. **AI Response Generation**

   * Using `gemini-1.5-flash`, the system generates 3 realistic responses a receptionist might say, along with 3 keywords for each response.

3. **Purpose of Providing Keywords**

   * **In real calls, users might miss the full response or hear it differently.**
   * To address this, **key phrases** are also provided so the user can grasp the meaning even with partial understanding.
   * For example: Words like "insurance card", "ID", "bring" help infer "what to bring".

4. **Voice Output Generation**

   * `gemini-2.0-flash-live-001` is used to **convert the responses and keywords into natural-sounding Korean speech**.
   * The audio is base64 encoded so it can be played immediately in the app.

5. **Multilingual Support**

   * The response is also translated back into the user's language, providing both **translated text and Korean audio** to maximize learning.

**Example Usage Flow:**

* User inputs “What documents do I need to bring?” in English
* The system translates it into Korean and generates possible receptionist responses
* The responses are read aloud in Korean
* Key phrases like “ID”, “insurance card”, “bring” are also voiced separately
* The translated response is displayed in the user's language

#### 💬 **Chatbot Feature (Text + Image RAG Chatbot)**

This feature is based on Gemini and RAG, providing **accurate and trustworthy responses** by analyzing **text or text + image** inputs. The responses are translated into the user’s language and enhanced using context from a vector database.  
**The RAG approach helps users avoid self-diagnosis or misjudgment and encourages proper hospital visits. It also reduces hallucination issues by referencing source documents.**

The chatbot’s **vector database (DB)** contains documents that cover both **vaccination-related information and multicultural policy guidance**, including:

* Vaccination precautions  
* Vaccine-specific information  
* Adverse reactions after vaccination  
* Adverse reactions by vaccine  
* Adverse reaction reporting criteria  
* Adverse reaction reporting procedures  
* Government policy guides (pregnancy, childbirth, child growth, dual-income, multi-child support, family support, etc.)

**📌 Model & Tech Stack**

| Item               | Description                                               |
|--------------------|-----------------------------------------------------------|
| **LLM Model**       | `gemini-1.5-flash`                                        |
| **RAG Components**  | `Langchain`, `FAISS`, `HuggingFaceEmbeddings`            |
| **Translation API** | `google-cloud-translate`                                 |
| **Embedding Model** | `BAAI/bge-base-en-v1.5`                                  |
| **Env Management**  | `dotenv`                                                  |
| **Note**            | For multimodal responses, both image + text prompts are passed to Gemini |

**🎯 Feature Description**

1. **Text-based RAG Chatbot (`generate_rag_response`)**

   * Receives a user question and retrieves similar document `chunks` from the vector DB
   * Sends the retrieved context and prompt to Gemini
   * Gemini generates a response, which is translated into the user’s language and returned

2. **Image-based Multimodal Chatbot (`generate_vision_response`)**

   * Sends both the user’s uploaded image and question to Gemini
   * Forms a multimodal prompt with the image + context retrieved from vector DB
   * Gemini returns a multimodal response, which is then translated into the user’s language

3. **Vector DB Search (`retrieve_relevant_chunks`)**

   * Searches FAISS for the 3 most relevant document chunks to the user’s query
   * Returns each chunk with its source and content to reinforce Gemini’s response

#### 💉 **Vaccine Certificate Validator**

This feature analyzes **certificate images** to automatically determine whether they match the specified vaccine name and vaccination period. It uses Gemini Vision API to **extract image-based information and perform logic validation**.

**📌 Model & Tech Stack**

| Item                    | Description                                                   |
|-------------------------|---------------------------------------------------------------|
| **LLM Model**           | `gemini-1.5-flash`                                             |
| **Vision Functionality**| Sends image + prompt to Gemini for OCR and condition matching |
| **Env Management**      | `dotenv`                                                      |
| **Date Parsing**        | `datetime`, `re`                                               |
| **Input Image Format**  | `image/png` (default), extensible if needed                   |

**🎯 Feature Description**

1. **Vaccine Info Verification from Image (`verify_vaccine_image`)**

   * Sends the image to Gemini to extract vaccine name and vaccination date
   * The prompt includes these conditions:
     * Vaccine name must match either the Korean or English name provided
     * The vaccination date must fall within the valid date range (`YYYY.MM.DD ~ YYYY.MM.DD`)
   * If Gemini responds `"True"`, all conditions are considered met

2. **Date Extraction (`extract_date`)**

   * Extracts the **first date in format YYYY.MM.DD** from the OCR result

3. **Date Validity Check (`is_date_in_range`)**

   * Checks whether the extracted date falls within the vaccination period
   * Returns `False` if there’s a format error or invalid date



---


### ✅ Privacy Policy

#### 1. Personal Information Collected

* User Information: Google account  
* Child Information: Name, gender, date of birth  
* Images (photos): When uploading images to the chatbot  
* Location Information: When using the vaccination center map feature  

#### 2. Method of Collecting Personal Information

* All personal information is collected **only after obtaining the user’s explicit consent**.  
* Location and gallery access are handled using the Android permission system in compliance with **Google Play’s privacy requirements**.  

#### 3. Purpose of Using Personal Information

* Providing personalized vaccination schedules for children  
* Offering map-based guidance to nearby vaccination centers  
* Enabling image/text-based consultation via chatbot features  

#### 4. Storage and Retention of Personal Information

* All data is stored **only on the user’s device (local database)**, not on any external server.  
* It is processed entirely within the app and **deleted when the app is uninstalled**.  

#### 5. Third-Party Sharing and External Transmission

* This app **does not transmit personal information to external servers or share it with third parties**.  


---


## 📱 Prototype Screens

### 🌐 Multilingual Support  
<table>
  <tr>
    <td align="center"><img src="https://github.com/user-attachments/assets/0c653c51-9c69-46a0-bc2a-857bec294329" height="450"/></td>
    <td align="center"><img src="https://github.com/user-attachments/assets/46064335-9785-46f9-a7f2-722eb4dab4e5" height="450"/></td>
    <td align="center"><img src="https://github.com/user-attachments/assets/79aedfb9-11f6-49d1-95c4-742d658c6545" height="450"/></td>
    <td align="center"><img src="https://github.com/user-attachments/assets/3c31503c-737a-42c9-9fb6-328c97d38f11" height="450"/></td>
  </tr>
  <tr>
    <td align="center">Language Selection</td>
    <td align="center">English</td>
    <td align="center">Korean</td>
    <td align="center">Japanese</td>
  </tr>
</table>


<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/697f722a-6512-4a50-a80c-12194ea2e0ec" height="500"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/08cf2edd-086d-49ec-9f7b-76393617f0d7" height="500"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/e82f0a0a-e82f-4931-a18a-4d5424bce6ba" height="500"/>
    </td>
  </tr>
  <tr>
    <td align="center">Chinese</td>
    <td align="center">Vietnamese</td>
    <td align="center">Thai</td>
  </tr>
</table>


#### 📌 Unique Features
Supports multiple languages including Korean, Vietnamese, Chinese, and Thai. Users receive translated sentences and pronunciation guides for better communication with medical staff.

#### ✅ Expectation Effectiveness
Helps multicultural caregivers feel less stressed when visiting clinics, ultimately improving access to medical services.

### 👶 Child-Specific Schedule Tracking  
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/752496fe-a816-467a-97fd-96a751791eed" height="650"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/82471c04-911c-4856-82d7-cfb9d7c2eb59" height="650"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/3b808370-48ec-442e-80f7-dadc3acda2b8" height="650"/>
    </td>
  </tr>
  <tr>
    <td align="center">Enter child information</td>
    <td align="center">View information by child</td>
    <td align="center">Check vaccination status for a specific child</td>
  </tr>
</table>

#### 📌 Unique Features
Caregivers can enter information for each child and manage individual vaccination schedules accordingly.

#### ✅ Expectation Effectiveness
Prevents missed vaccinations and improves health management for families with multiple children.


### 📍 Nearby Vaccination Center Finder 
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/54a79d90-192a-480a-9ffa-0d48acd0c07a" height="700"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/6f1666b5-e472-4588-91d1-0adff9858f6a" height="700"/>
    </td>
  </tr>
  <tr>
    <td align="center">Map View</td>
    <td align="center">Vaccination Center Detail</td>
  </tr>
</table>


#### 📌 Unique Features
Shows nearby clinics and health centers offering vaccinations based on user location and availability.

#### ✅ Expectation Effectiveness
Reduces effort to search for clinics and helps users get vaccinated sooner, saving time and stress.

### ☎️ Call Script Generator  
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/5b970ede-662c-4c4c-baca-61fb925fdb4d" height="650"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/00f2e940-54ad-4a91-bd93-4e89c8c4ec29" height="650"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/563c7ab5-5f8b-4ba4-bdaa-2182068df4e4" height="650"/>
    </td>
  </tr>
  <tr>
    <td align="center">Suggestions View</td>
    <td align="center">Suggested Answer & Korean Translation #1</td>
    <td align="center">Suggested Answer & Korean Translation #2</td>
  </tr>
</table>

#### 📌 Unique Features
Users type in what they want to ask the clinic, and AI generates a polite Korean version with suggested answers and pronunciation tips for key words.

#### ✅ Expectation Effectiveness
Pre-written scripts reduce call anxiety, while keyword prompts help users speak confidently with minimal language.

### 🤖 Reliable AI Chatbot for Vaccine & Policy Help  
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/c1bb5e09-6c60-46f4-a9c6-c7778cae7c94" height="700"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/25e192f9-5c69-45f4-b76b-6cefd01498ef" height="700"/>
    </td>
  </tr>
  <tr>
    <td align="center">Response to vaccination questions</td>
    <td align="center">Response to adverse reaction images</td>
  </tr>
</table>

#### 📌 Unique Features
The AI chatbot offers basic vaccination info—like types, schedules, and side effects—along with support program details for multicultural families.
When users upload a photo of a possible adverse reaction, it provides a response guide based on official medical sources.
Powered by RAG, the chatbot reduces hallucinations and delivers reliable answers.

#### ✅ Expectation Effectiveness
Providing reliable information on vaccinations and multicultural support policies, along with analyzing photos of adverse reactions to prompt timely medical responses,
can significantly enhance user health protection and improve access to information.

### 💉 Vaccine Log & Symptom Tracker  
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/dd6de007-951f-415e-84c7-5ac06bb868cb" height="650"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/2b01f18a-6f14-4f87-ab1c-9b769c963bc1" height="650"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/7411df76-de9f-455f-9a46-e1b9c0322429" height="650"/>
    </td>
  </tr>
  <tr>
    <td align="center">View information by child</td>
    <td align="center">View full vaccination schedule</td>
    <td align="center">Check upcoming vaccination appointments</td>
  </tr>
</table>

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/702563e4-849a-4a65-bbfb-91903751268a" height="700"/>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/16e5a0d3-5c71-4818-a2b7-dc8f37f7df8c" height="700"/>
    </td>
  </tr>
  <tr>
    <td align="center">Upload image for vaccination verification</td>
    <td align="center">Confirm vaccination completion through verification</td>
  </tr>
</table>

#### 📌 Unique Features
Users can track vaccination status and log symptoms like fever or rash.
When a certificate image is uploaded, Gemini compares it with the child’s info and automatically updates the record.

#### ✅ Expectation Effectiveness
With AI-organized records, caregivers avoid manual tracking and ensure accurate, informed consultations.
This reduces both the risk of missed or duplicate vaccinations and the burden of managing records.


---


## 🌱 Expected Effects

1. **Reduction in Missed Vaccinations Due to Language Barriers**  
   By providing vaccination information in the guardian’s native language, the app minimizes **missed schedules, misinterpretation of information, and anxiety** caused by language barriers.  
   As a result, **on-time vaccination rates improve**, contributing to the prevention of infectious diseases.

2. **Improved Access to Public Health Policies**  
   Offering local medical support policies, public health center info, and vaccination guidelines in the user’s language increases **accessibility and utilization of public healthcare services**.  
   Enhanced access to information positively affects **health equity for multicultural families**.

3. **Efficient Health Management for Each Child**  
   Guardians can **centrally manage vaccination schedules, vaccine types, and adverse reaction records** for each child within the app, enabling **systematic health management without missing key dates**.  
   Even in households with multiple children or complex vaccination histories, **individual tracking becomes easier**.

4. **Restoration of Trust Between Users and the Healthcare System**  
   By receiving accurate information via the AI chatbot and practicing real-life phone conversations through the CallScript feature, guardians can **overcome psychological barriers before visiting medical institutions**.  
   This leads to **reduced distrust and restored confidence in the healthcare system**, ultimately increasing participation and satisfaction in medical services.

5. **Enhanced Access to Welfare Through Multicultural Policy Information**  
   Beyond vaccinations, the app provides **government policy information on pregnancy, childbirth, childcare, and education** for multicultural families in their native language, enhancing **access and utilization of welfare services**.  
   This becomes a **practical channel for supporting guardians in administrative blind spots**.



---


## 🔚 Closing

### Future Feature Additions

1. Localized health policy notification system  
2. Additional language support (e.g., Uzbek, Nepali, Indonesian)  
3. In-app multilingual pre-vaccination form support  
4. Community feature for information sharing among multicultural families  
5. Integration with hospital appointment systems  

#### Multicultural Population by Nationality (2023)

| Nationality        | Population (persons) |
|--------------------|----------------------|
| Korean-Chinese     | 242,032              |
| Chinese            | 205,502              |
| Vietnamese         | 227,930              |
| Uzbek              | 55,239               |
| Filipino           | 48,648               |
| Cambodian          | 53,904               |
| Nepalese           | 60,663               |
| Indonesian         | 50,334               |
| Thai               | 40,062               |
| Burmese (Myanmar)  | 39,630               |
| American           | 32,532               |
| Mongolian          | 32,466               |
| Japanese           | 27,381               |
| Sri Lankan         | 28,258               |
| Taiwanese          | 17,704               |
| Kazakh             | 20,203               |

### Global Expansion Potential

* By actively utilizing **Google-based APIs (Gemini, Translate, Maps, etc.)**, MARO can evolve into a **global healthcare platform for immigrants and foreign guardians**.  
* It can be applied not only in Korea but also in countries with vaccination systems such as **the United States, Japan, Germany, and Australia**.  
  - The structure’s compatibility with **multilingual translation**, **location-based information**, and **hospital API integration** allows for **flexible adoption within various national public health systems**.


## 👩🏻‍⚕️Contributors
|Member| Jeong Eunji | Lee SeungEun | Lee Yeso | Lee YuJi |
|:--:|:--:|:--:|:--:|:--:|
|Role| (PM) AI/ML | Server | Android | UX/UI |
|Profile|  <img src= "https://github.com/user-attachments/assets/7ea1888d-08b3-4d57-a7c1-35cb2a84b165" width="100" height="100"/>     | <img src= "https://github.com/user-attachments/assets/408feef0-bcbe-4aad-92e1-152a249f781f" width="100" height="100"/>   | <img src= "https://github.com/user-attachments/assets/436ed6b4-2ec8-4d46-a162-0c26dd6ebb72" width="100" height="100"/>  | <img src= "https://github.com/user-attachments/assets/0a8c0762-ae64-483b-9ee2-154f27f04b96" width="100" height="100"/>   |
