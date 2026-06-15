import requests

# PASTE YOUR ACTUAL, LONG ACCESS TOKEN STRING FROM THE DEVELOPER PORTAL HERE
ACCESS_TOKEN = "AQUgHeAMkkE6tBmUXJXNqwjF4bJ8P8MfkAo8cTTOSu6pnahm7vi6ZPRdvDbB1cIHoYQsSt6AZ5IzpGI888nPRgP-C04BmVaYXaV51tqyayP05bJOz-wOQ_tCaT4CWKvlnQgCEKGpiT6pRglEm1jUclqReeSDnlbx6Of1n5JMudGbni9911fj7x65MfB_FzqzPbRJMUj_JOOtZfrelxunMkj8-dy8UJBsixTR90wT1K-9cJ1bqofO3mpKwRhgUyQXxHq7-zADbMQON0HZ0wgyv9pst0zFlwK35M9uR69kU5oDSI5Xzw_36dAW7iiSheIr7mWajuuqd65DssrVRIZvlhIRdmB0Aw"
def get_my_urn_now():
    # Force the strict, explicit API path URL string
    url = "https://linkedin.com"
    
    headers = {
        "Authorization": f"Bearer {ACCESS_TOKEN}",
        "X-Restli-Protocol-Version": "2.0.0",
        "Content-Type": "application/json"
    }

    print("📡 Sending profile lookup to secure API server...")
    try:
        response = requests.get(url, headers=headers)
        
        print(f"📊 Status Code: {response.status_code}")
        
        if response.status_code == 200:
            data = response.json()
            person_id = data.get("id")
            person_urn = f"urn:li:person:{person_id}"
            
            print("\n" + "="*50)
            print("🎉 SUCCESS! IDENTITY RESOLVED:")
            print(f"👤 Account Name: {data.get('localizedFirstName', '')} {data.get('localizedLastName', '')}")
            print(f"🔑 Your Exact Person URN: {person_urn}")
            print("="*50)
            print("\n➡️ Next step: Copy that exact URN string and put it in your .env file!")
            return person_urn
        else:
            print(f"❌ Error text returned from API: {response.text}")
    except Exception as e:
        print(f"⚠️ Connection failure: {e}")

if __name__ == "__main__":
    get_my_urn_now()
