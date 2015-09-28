#include <boost/crc.hpp>
#include <iostream>
#include <map>
#include <string>
#include <time.h>
#include <sstream>
#include <ctime>

void genNextString(std::vector<char> &v);
std::string getCheckSum(std::string s, int &value);

int main ()
{
    std::map<std::string, std::string> mymap;
    std::map<std::string, int> m;
    bool found = false;

    std::vector<char> v;
    v.push_back('0');

    // track time
    clock_t begin = clock();

    int myHashCSInt;
    std::string myHash = "f0878b056247191f7fcfa6642c2d58de";
    std::string myHashCS = getCheckSum(myHash, myHashCSInt);

    std::cout << "Myhash: " << myHash << " Checksum: " << myHashCS << " Int value: " << myHashCSInt << std::endl;
    mymap.insert(std::pair<std::string, std::string> (myHashCS, myHash));
    //mymap.insert(std::pair<std::string, std::string> ("0x399d9dec", "testhash"));

    long long i = 0;
    while (!found) {
        int len = 1 + rand() % 10;
        std::string str(v.begin(), v.end());
        //std::cout << str << std::endl;
    
        int csIntVal;
        std::string cs = getCheckSum(str, csIntVal);
        std::map<std::string, std::string>::iterator it = mymap.find(cs);
        ++i; 

        // found duplicate
        if (it != mymap.end()) {
            clock_t end = clock();
            double elapsed = double(end - begin);
            std::cout << "Elapsed time: " << elapsed / CLOCKS_PER_SEC << std::endl;

            std::cout << "Checksum: " << cs << std::endl;
            std::cout << "Checksum IntVal: " << csIntVal << std::endl;
            std::cout << "First String: " << mymap[cs] << std::endl;
            std::cout << "Second String: " << str << std::endl;
            std::cout << "Map Size: " << mymap.size() << std::endl;
            std::cout << "Number checked: " << i << std::endl;
            found = true;
        } else {
            //mymap.insert(std::pair<std::string, std::string> (cs, str));
            //m.insert(std::pair<std::string, int> (cs, csIntVal));
            genNextString(v);
        }
        
        if (mymap.size() % 1000000 == 0 || (i % 100000000) == 0) {
            //std::cout << "Checkmark Checked: " << mymap.size() << " " << str << std::endl;
            std::cout << "Checkmark Checked: " << i << " " << str << std::endl;
        }
    }
    return 0;
}

std::string getCheckSum(std::string s, int &val) {
    boost::crc_32_type r;
    r.process_bytes(s.data(), s.length());
    std::stringstream stream;
    val = r.checksum();
    stream << std::hex << r.checksum();
    std::string rs (stream.str());
    return "0x" + rs;
}

// Iterate through alphanumeric strings (only lowercase letters)
void genNextString(std::vector<char> &v) {
    static const char alphanum[] =
        "0123456789"
        "abcdefghijklmnopqrstuvwxyz";

    bool add = false;
    int i = v.size() - 1;

    do {
        if (v[i] == '9') {
            v[i] = 'a' ;
            add = false;
        } else if (v[i] == 'z') {
            v[i] = '0';
            add = true;
        } else {
            v[i] = (char)((int)v[i] + 1);
            add = false;
        }
        --i;
    } while (add && i >= 0);
    
    if (add) {
        v.insert(v.begin(), '0');
    }
}
