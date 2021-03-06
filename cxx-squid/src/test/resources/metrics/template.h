/**
 testClass
*/
template<typename T>
class testClass
{
public:
	void publicMethod();

	int publicAttr;

    // Issue #928 (should be marked undocumented)
    using type = void;
};

template<typename Second>
struct second_struct {
    /*!
     * \brief Issue #928 (should be marked documented)
     */
    using alpha = float;

    /*!
     * \brief Issue #928 (should be marked documented)
     */
    using beta = double;
};

/*!
 * \brief issue #736
 */
template <typename T>
using intrinsic_type = typename intrinsic_traits<T>::intrinsic_type;

template <typename T>
using inline_intrinsic_type = typename intrinsic_traits<T>::intrinsic_type; ///< issue #736
